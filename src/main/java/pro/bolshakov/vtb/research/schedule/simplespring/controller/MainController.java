package pro.bolshakov.vtb.research.schedule.simplespring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.bolshakov.vtb.research.schedule.simplespring.service.DeliveryService;
import pro.bolshakov.vtb.research.schedule.simplespring.service.ProductClaimService;

@RestController
public class MainController {

    private long ind = 0;
    private final DeliveryService deliveryService;
    private final ProductClaimService productClaimService;

    public MainController(DeliveryService deliveryService,
                          ProductClaimService productClaimService) {
        this.deliveryService = deliveryService;
        this.productClaimService = productClaimService;
    }

    @RequestMapping("/test")
    public void test(@RequestParam(name = "attempts") int attempts){
        long id = ++ind;
        System.out.println("Get test task with ID-> " + id + " attempts -> " + attempts);
        deliveryService.addTestTask(id, attempts);
        productClaimService.addNewTask(id);
    }
}
