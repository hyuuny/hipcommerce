package com.hipcommerce.members.domain;

import com.hipcommerce.members.dto.MemberDto.DetailedSearchCondition;
import com.hipcommerce.members.dto.MemberSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

  Page<MemberSearchDto> retrieveMember(DetailedSearchCondition searchCondition, Pageable pageable);

}
