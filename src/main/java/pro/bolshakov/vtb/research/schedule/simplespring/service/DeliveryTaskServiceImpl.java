package pro.bolshakov.vtb.research.schedule.simplespring.service;

import org.springframework.stereotype.Service;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class DeliveryTaskServiceImpl implements DeliveryTaskService {

    private final BlockingQueue<ProductClaimDeliveryTask> taskQueue = new LinkedBlockingDeque<>();

    @Override
    public ProductClaimDeliveryTask getTask() {
        try {
            return taskQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void putTask(ProductClaimDeliveryTask task) {
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
