package pro.bolshakov.vtb.research.schedule.simplespring.service;

import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;

public interface DeliveryTaskService {
    ProductClaimDeliveryTask getTask();
    void putTask(ProductClaimDeliveryTask task);
}
