package pro.bolshakov.vtb.research.schedule.simplespring.service;

import org.springframework.stereotype.Service;
import pro.bolshakov.vtb.research.schedule.simplespring.domain.ProductClaimDeliveryTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private Map<Long, Integer> cache = new ConcurrentHashMap<>();

    @Override
    public boolean send(ProductClaimDeliveryTask task) {
        System.out.println("Trying to delivery task : " + task.getId());
        Integer goalAttempts = cache.get(task.getId());
        return task.getAttempt() >= goalAttempts;
    }

    @Override
    public void addTestTask(long id, int attempts) {
        cache.put(id, attempts);
    }
}
