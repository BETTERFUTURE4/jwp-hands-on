package com.example.etag;

import static com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean =
                new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/etag");
        filterRegistrationBean.addUrlPatterns(PREFIX_STATIC_RESOURCES + "/*");

        return filterRegistrationBean;
    }
}
