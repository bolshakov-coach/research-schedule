package pro.bolshakov.vtb.research.schedule.simplespring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;
import pro.bolshakov.vtb.research.schedule.simplespring.service.DeliveryEngine;
import pro.bolshakov.vtb.research.schedule.simplespring.service.DeliveryService;
import pro.bolshakov.vtb.research.schedule.simplespring.service.DeliveryTaskService;
import pro.bolshakov.vtb.research.schedule.simplespring.service.ProductClaimService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.*;

@Component
public class ScheduleConfig {

    @Value("${attempts.limit}")
    private int maxAttempts;
    @Value("${attempts.delays}")
    private String strDelays;

    private final ProductClaimService productClaimService;
    private final DeliveryTaskService deliveryTaskService;
    private final DeliveryService deliveryService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScheduleConfig(ProductClaimService productClaimService, DeliveryTaskService deliveryTaskService, DeliveryService deliveryService) {
        this.productClaimService = productClaimService;
        this.deliveryTaskService = deliveryTaskService;
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
        // change status TAKEN
        for (ProductClaimDeliveryTask task : preparedForSending) {
            deliveryTaskService.putTask(task);
        }
    }


    public DeliveryEngine createDeliveryEngine(){
        return new DeliveryEngine(maxAttempts, parseDelays(),
                deliveryTaskService, productClaimService, deliveryService);
    }

    private int[] parseDelays() {
        String[] split = strDelays.split(",");
        int[] delays = new int[split.length + 1];
        delays[0] = 0;
        for (int i = 0; i < split.length; i++) {
            delays[i + 1] = Integer.parseInt(split[i]);
        }
        return delays;
    }



}
