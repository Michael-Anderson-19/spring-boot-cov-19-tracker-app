package anderson.michael.CovidTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //tell spring to look for schedule annotated classes andrun when specified
public class CovidTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CovidTrackerApplication.class, args);
    }

}
