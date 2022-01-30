package com.hipcommerce.config.security.components;

import com.hipcommerce.members.service.MemberAdapter;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityAuditorAware implements AuditorAware<Long> {

  @Override
  public Optional<Long> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .filter(Authentication::isAuthenticated)
      .filter(authentication -> !AnonymousAuthenticationToken.class
        .isAssignableFrom(authentication.getClass()))
      .map(Authentication::getPrincipal)
      .filter(principal -> MemberAdapter.class
        .isAssignableFrom(principal.getClass()))
      .map(MemberAdapter.class::cast)
      .map(MemberAdapter::getUserId);
  }

}
