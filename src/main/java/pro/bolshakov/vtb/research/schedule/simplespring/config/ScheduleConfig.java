package pro.bolshakov.vtb.research.schedule.simplespring.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.DeliveryStatus;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;
import pro.bolshakov.vtb.research.schedule.simplespring.service.DeliveryService;
import pro.bolshakov.vtb.research.schedule.simplespring.service.ProductClaimService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.*;

@Component
public class ScheduleConfig {

    private final static int MAX_ATTEMPT = 3;
    private final static long[] DELAY_BETWEEN_ATTEMPTS = {1000, 15000};

    private final ProductClaimService productClaimService;
    private final DeliveryService deliveryService;

    private final BlockingQueue<ProductClaimDeliveryTask> taskQueue = new LinkedBlockingDeque<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScheduleConfig(ProductClaimService productClaimService, DeliveryService deliveryService) {
        this.productClaimService = productClaimService;
        this.deliveryService = deliveryService;
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("run delivery engine");
        executor.submit(createDeliveryEngine());
    }

    @PreDestroy
    public void destroy(){
        executor.shutdown();
    }

    @Scheduled(fixedDelay = 15000)
    public void pollerSendingStatusesProductClaim(){
        System.out.println("Execute polling of task");
        List<ProductClaimDeliveryTask> preparedForSending = productClaimService.getPreparedForSending();
        System.out.println("Got tasks -> " + preparedForSending.size());
        for (ProductClaimDeliveryTask task : preparedForSending) {
            try {
                taskQueue.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private Runnable createDeliveryEngine(){
        return new Runnable() {
            @Override
            public void run() {
                thread_loop:
                while (true){

                    ProductClaimDeliveryTask task;
                    try {
                        task = taskQueue.take();
                        System.out.println("Delivery Engine got task -> " + task.getId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    //can be checking current status
                    task = productClaimService.setStatus(task, DeliveryStatus.SENDING);

                    int attempts = 0;
                    boolean delivered = false;
                    while (!delivered && task.getAttempt() < MAX_ATTEMPT){
                        if(task.getAttempt() > 0){
                            System.out.println("*** Pause between attempt");
                            try {
                                Thread.sleep(DELAY_BETWEEN_ATTEMPTS[task.getAttempt() - 1]);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break thread_loop;
                            }
                        }
                        System.out.println("try to send task " + task.getId() + " attempts -> " + attempts);
                        delivered = deliveryService.send(task);
                        if(!delivered){
                            System.out.println("not delivered " + task.getId());
                            task = productClaimService.increaseAttempt(task);
                        }
                    }

                    if(!delivered){
                        task = productClaimService.setStatus(task, DeliveryStatus.FAIL);
                    }
                    else {
                        task = productClaimService.setStatus(task, DeliveryStatus.DELIVERED);
                    }
                }
            }
        };
    }

}
