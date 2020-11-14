package me.muphy;

import me.muphy.servicce.TableService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DownloadApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DownloadApplication.class, args);
        TableService tableService = context.getBean(TableService.class);
        tableService.createAllEntity("hacker");
    }

}
