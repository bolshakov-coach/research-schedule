package pro.bolshakov.vtb.research.schedule.simplespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimpleSpringScheduleApp {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSpringScheduleApp.class, args);
    }

}
