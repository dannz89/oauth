package com.ddt.oauth.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("forward:/index.html");
        registry.addViewController("/error").setViewName("forward:/error.html");
        registry.addViewController("/secret").setViewName("forward:/secret.html");
        registry.addViewController("/logged_out").setViewName("forward:/index.html");
    }
}
