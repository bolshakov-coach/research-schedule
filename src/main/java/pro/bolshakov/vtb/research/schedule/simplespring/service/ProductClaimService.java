package pro.bolshakov.vtb.research.schedule.simplespring.service;

import pro.bolshakov.vtb.research.schedule.simplespring.domain.DeliveryStatus;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;

import java.util.List;

public interface ProductClaimService {
    List<ProductClaimDeliveryTask> getPreparedForSending();
    ProductClaimDeliveryTask setStatus(ProductClaimDeliveryTask task, DeliveryStatus status);
    ProductClaimDeliveryTask increaseAttempt(ProductClaimDeliveryTask task);
    void addNewTask(long id);
}
