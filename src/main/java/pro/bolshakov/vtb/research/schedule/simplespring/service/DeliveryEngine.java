package pro.bolshakov.vtb.research.schedule.simplespring.service;

import pro.bolshakov.vtb.research.schedule.simplespring.domain.DeliveryStatus;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

public class DeliveryEngine implements Runnable{

    private final int maxAttempts;
    private final int[] delays;
    private final DeliveryTaskService deliveryTaskService;
    private final ProductClaimService productClaimService;
    private final DeliveryService deliveryService;

    public DeliveryEngine(int maxAttempts,
                          int[] delays,
                          DeliveryTaskService deliveryTaskService,
                          ProductClaimService productClaimService,
                          DeliveryService deliveryService) {
        this.maxAttempts = maxAttempts;
        this.delays = delays;
        this.deliveryTaskService = deliveryTaskService;
        this.productClaimService = productClaimService;
        this.deliveryService = deliveryService;
    }

    private ScheduledExecutorService executorService;

    @PreDestroy
    public void destroy(){
        if(executorService != null){
            executorService.shutdown();
        }
    }

    @Override
    public void run() {

        if(executorService != null){
            executorService.shutdown();
        }
        executorService = Executors.newSingleThreadScheduledExecutor();

        while (true){

            ProductClaimDeliveryTask task = deliveryTaskService.getTask();
            if(task == null){
                System.out.println("Something does wrong");
                continue;
            }
            System.out.println("Delivery Engine got task -> " + task.getId());

            //can be checking current status
            task = productClaimService.setStatus(task, DeliveryStatus.SENDING);

            boolean delivered = false;
            while (!task.isDelivered() && task.getAttempt() < maxAttempts){
                int delay = delays[task.getAttempt()];
                if(delay > 0){
                    System.out.println("*** Pause between attempt seconds -> " + (delay));
                }
                ScheduledFuture<ProductClaimDeliveryTask> future =
                        executorService.schedule(new CallDelivery(task), delay, TimeUnit.SECONDS);

                try {
                    task = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("not delivered " + task.getId());
                    e.printStackTrace();
                    task = productClaimService.increaseAttempt(task);
                }
            }

            if(!task.isDelivered()){
                task = productClaimService.setStatus(task, DeliveryStatus.FAIL);
            }
        }

    }

    private class CallDelivery implements Callable<ProductClaimDeliveryTask>{

        private final ProductClaimDeliveryTask task;

        public CallDelivery(ProductClaimDeliveryTask task) {
            this.task = task;
        }

        @Override
        public ProductClaimDeliveryTask call() throws Exception {
            System.out.println("try to send task " + task.getId() + " attempts -> " +
                    (task.getAttempt() + 1));
            boolean delivered = deliveryService.send(task);
            if(!delivered){
                System.out.println("not delivered " + task.getId());
                return productClaimService.increaseAttempt(task);
            }
            return productClaimService.setStatus(task, DeliveryStatus.DELIVERED);
        }
    }
}
