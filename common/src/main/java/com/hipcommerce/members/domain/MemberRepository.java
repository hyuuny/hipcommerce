package com.hipcommerce.members.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  Optional<Member> findByUsername(String username);

  Optional<Member> findByEmail(final String email);

}
