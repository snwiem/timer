package org.syracus.timerws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class TimerWsApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TimerWsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TimerWsApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        LOG.info("Creating CORS configuration");

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }
}
