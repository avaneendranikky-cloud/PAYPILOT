package com.paypilot.payments.config;
import com.paypilot.payments.service.DateRangeValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration public class WebConfig implements WebMvcConfigurer {
 private final DateRangeValidator validator; public WebConfig(DateRangeValidator v){validator=v;}
 @Override public void addInterceptors(InterceptorRegistry registry){registry.addInterceptor(validator).addPathPatterns("/api/v1/users/*/payments/**");}
}
