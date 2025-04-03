package com.example.dailyschedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = {"com.example.dailyschedule"})
@EnableWebMvc
public class DailyScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyScheduleApplication.class, args);
    }

}
