package com.jinju.FirmwareServiceTransfer.configurations;

import com.jinju.FirmwareServiceTransfer.interceptors.CheckInterceptor;
import com.jinju.FirmwareServiceTransfer.interceptors.FlowLimiterInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Value("${firmwareFilePath}")
    String firmwareFilePath;

    @Bean
    public CheckInterceptor checkInterceptor(){
        return new CheckInterceptor();
    }

    @Bean
    public FlowLimiterInterceptor flowLimiterInterceptor(){
        return new FlowLimiterInterceptor();
    }


    // flow control
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // download flow control
        registry.addInterceptor(checkInterceptor())
                .addPathPatterns("/files/**");
        // api flow limiter
        registry.addInterceptor(flowLimiterInterceptor())
                .addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    // extend firmware location
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // relocation file path
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:"+firmwareFilePath);
        super.addResourceHandlers(registry);
    }
}
