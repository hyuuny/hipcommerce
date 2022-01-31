package com.hipcommerce.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.config.security.filter.JwtAuthenticationFilter;
import com.hipcommerce.config.security.filter.JwtAuthorizationFilter;
import com.hipcommerce.config.security.handler.JwtAccessDeniedHandler;
import com.hipcommerce.config.security.handler.JwtAuthenticationEntryPoint;
import com.hipcommerce.config.security.provider.JwtAuthenticationProvider;
import com.hipcommerce.config.security.service.AuthenticationService;
import com.hipcommerce.config.security.utils.JwtUtil;
import com.hipcommerce.members.port.AuthPort;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String AUTHENTICATION_URL = "/auth";

  private final JwtAuthenticationProvider jwtAuthenticationProvider;
  private final JwtUtil jwtUtil;
  private final AuthenticationService authenticationService;
  private final ObjectMapper objectMapper;
  private final AuthPort authPort;

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(jwtAuthenticationProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(
            SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(
            HttpServletResponse.SC_UNAUTHORIZED))
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .accessDeniedHandler(jwtAccessDeniedHandler)
        .and()
        .addFilterBefore(buildJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(buildJwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/**").permitAll()
        .antMatchers(
            HttpMethod.POST,
            "/auth",
            "/api/v1/members",
            "/api/v1/members/find-password",
            "/api/v1/members/reset-password"
        ).permitAll()
        .antMatchers(
            "/payments/**",
            "/test/**"
        ).permitAll()
        .anyRequest().authenticated()
        .and()
    ;

  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers(
        "/docs/index.html",
        "/v2/api-docs",
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/webjars/**",
        "/swagger/**",
        "/h2-console/**",
        "/favicon.ico"
    );
  }

  private JwtAuthenticationFilter buildJwtAuthenticationFilter()
      throws Exception {
    JwtAuthenticationFilter filter =
        new JwtAuthenticationFilter(AUTHENTICATION_URL, authenticationService, objectMapper, authPort);
    filter.setAuthenticationManager(this.authenticationManager());
    return filter;
  }

  private JwtAuthorizationFilter buildJwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtUtil, objectMapper);
  }

}
