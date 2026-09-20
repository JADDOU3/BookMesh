package org.example.bookmesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BookMeshApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookMeshApplication.class, args);
    }

}
