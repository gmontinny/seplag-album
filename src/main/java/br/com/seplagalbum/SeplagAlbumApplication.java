package br.com.seplagalbum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeplagAlbumApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeplagAlbumApplication.class, args);
    }

}
