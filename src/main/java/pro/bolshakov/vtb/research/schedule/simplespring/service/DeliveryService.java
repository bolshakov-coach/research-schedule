package pro.bolshakov.vtb.research.schedule.simplespring.service;

import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;

public interface DeliveryService {
    boolean send(ProductClaimDeliveryTask task);

    void addTestTask(long id, int attempts);
}
