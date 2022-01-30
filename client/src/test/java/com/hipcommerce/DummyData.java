package com.hipcommerce;

import com.google.common.collect.Lists;
import com.hipcommerce.categories.dto.CategoryDto;
import com.hipcommerce.members.domain.Member.Gender;
import com.hipcommerce.members.dto.MemberDto.SignUpRequest;
import com.hipcommerce.product.domain.Product.Status;
import com.hipcommerce.product.dto.ProductDto;
import com.hipcommerce.product.dto.ProductOptionDto.Create;

public class DummyData {

  public static final String USER_EMAIL = "user@naver.com";
  public static final String USER_PASSWORD = "aa123456";

  public static final String ADMIN_EMAIL = "admin";
  public static final String ADMIN_PASSWORD = "aa123123";

  public static SignUpRequest.SignUpRequestBuilder anAdmin() {
    return SignUpRequest.builder()
        .email(ADMIN_EMAIL)
        .password(ADMIN_PASSWORD)
        .name("관리자")
        .mobilePhone("01012341234")
        .gender(Gender.MALE);
  }

  public static SignUpRequest.SignUpRequestBuilder anUser() {
    return SignUpRequest.builder()
        .email(USER_EMAIL)
        .password(USER_PASSWORD)
        .name("회원")
        .mobilePhone("01045674567")
        .gender(Gender.MALE);
  }

  public static CategoryDto.Create.CreateBuilder aCategory() {
    return CategoryDto.Create.builder()
        .name("아우터")
        .level(1)
        .priorityNumber(1)
        .iconImageUrl("https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg")
        ;
  }

  public static ProductDto.Create.CreateBuilder aProduct() {
    return ProductDto.Create.builder()
        .name("체크셔츠")
        .brand("톰브라운")
        .name("22년 톰브라운 체크셔츠")
        .tag("#체크|#봄에최고|#가을도최고")
        .price(36800L)
        .discountPrice(10000L)
        .priorityNumber(1)
        .thumbnail(
            "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg")
        .status(Status.ON_SALE)
        .options(Lists.newArrayList(
            Create.builder()
                .name("베이지")
                .imageUrl(
                    "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg")
                .stockQuantity(130)
                .build(),
            Create.builder()
                .name("블랙")
                .price(1500L)
                .imageUrl(
                    "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg")
                .stockQuantity(200)
                .build(),
            Create.builder()
                .name("레드")
                .imageUrl(
                    "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg")
                .stockQuantity(200)
                .build()
        ));
  }

}
