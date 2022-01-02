package com.hipcommerce.common;

import com.hipcommerce.common.web.interceptors.LogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LogInterceptor())
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/css/**",
            "/js/**",
            "/fonts/**",
            "/img/**",
            "/images/**",
            "/webfonts/**",
            "/i18n/**",
            "/favicon.ico",
            "/assets/**",
            "/static-bundle/**"
        );
    registry.addInterceptor(localeChangeInterceptor());
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    return localeChangeInterceptor;
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
    rs.setBasename("i18n/messages");
    rs.setDefaultEncoding("UTF-8");
    rs.setUseCodeAsDefaultMessage(true);
    return rs;
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver localeResolver = new CookieLocaleResolver();
    localeResolver.setCookieName("i18n");
    localeResolver.setCookieMaxAge(-1);
    localeResolver.setCookiePath("/");
    return localeResolver;
  }

  @Bean
  public MessageSourceAccessor messageSourceAccessor() {
    return new MessageSourceAccessor(messageSource());
  }

}
