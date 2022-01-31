package com.hipcommerce.members.domain;

import static com.hipcommerce.members.domain.QMember.member;
import static com.querydsl.core.types.Projections.fields;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.hipcommerce.common.jpa.support.Querydsl4RepositorySupport;
import com.hipcommerce.members.domain.Member.Gender;
import com.hipcommerce.members.domain.Member.Status;
import com.hipcommerce.members.dto.MemberDto.DetailedSearchCondition;
import com.hipcommerce.members.dto.MemberSearchDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class MemberRepositoryImpl extends Querydsl4RepositorySupport implements MemberRepositoryCustom{

  public MemberRepositoryImpl() {
    super(Member.class);
  }

  @Override
  public Page<MemberSearchDto> retrieveMember(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(MemberSearchDto.class,
        ExpressionUtils.as(member, "member")
    ))
        .from(member)
        .where(
            statusEq(searchCondition.getStatus()),
            emailEq(searchCondition.getEmail()),
            mobilePhoneEq(searchCondition.getMobilePhone()),
            nameEq(searchCondition.getName()),
            genderEq(searchCondition.getGender())
        ));

  }

  private BooleanExpression statusEq(final Status status) {
    return isEmpty(status) ? null : member.status.eq(status);
  }

  private BooleanExpression emailEq(final String email) {
    return isEmpty(email) ? null : member.email.eq(email);
  }

  private BooleanExpression mobilePhoneEq(final String mobilePhone) {
    return isEmpty(mobilePhone) ? null : member.mobilePhone.eq(mobilePhone);
  }

  private BooleanExpression nameEq(final String name) {
    return isEmpty(name) ? null : member.name.eq(name);
  }

  private BooleanExpression genderEq(final Gender gender) {
    return isEmpty(gender) ? null : member.gender.eq(gender);
  }
}
