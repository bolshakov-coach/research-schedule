package pro.bolshakov.vtb.research.schedule.simplespring.service;

import org.springframework.stereotype.Service;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.DeliveryStatus;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ProductClaimServiceImpl implements ProductClaimService {

    private Map<Long, ProductClaimDeliveryTask> cache = new ConcurrentHashMap<>();

    @Override
    public List<ProductClaimDeliveryTask> getPreparedForSending() {
        return cache.values().stream()
                .filter(task -> DeliveryStatus.NEW.equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public ProductClaimDeliveryTask setStatus(ProductClaimDeliveryTask task, DeliveryStatus status) {
        System.out.println("**** Set status -> " + status + " for task " + task.getId());
        return putTask(task.setStatus(status));
    }

    @Override
    public ProductClaimDeliveryTask increaseAttempt(ProductClaimDeliveryTask task) {
        System.out.println("Increased attempts for task -> " + task.getId() +
                " from " + task.getAttempt() +
                " to " + (task.getAttempt() + 1));
        return putTask(task.setAttempt(task.getAttempt() + 1));
    }

    @Override
    public void addNewTask(long id) {
        putTask(new ProductClaimDeliveryTask(id));
    }

    private ProductClaimDeliveryTask putTask(ProductClaimDeliveryTask task){
       cache.put(task.getId(), task);
       return task;
    }

}
