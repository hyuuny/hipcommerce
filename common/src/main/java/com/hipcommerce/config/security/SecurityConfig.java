package com.hipcommerce.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.config.security.filter.JwtAuthorizationFilter;
import com.hipcommerce.config.security.handler.JwtAccessDeniedHandler;
import com.hipcommerce.config.security.handler.JwtAuthenticationEntryPoint;
import com.hipcommerce.config.security.utils.JwtUtil;
import com.hipcommerce.config.security.service.AuthService;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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

  private final JwtUtil jwtUtil;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final AuthService authService;
  private final ObjectMapper objectMapper;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
        .sessionManagement().sessionCreationPolicy(
            SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(
            HttpServletResponse.SC_UNAUTHORIZED))
        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 진입점
        .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패 진입점
        .and()
        .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
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

}
