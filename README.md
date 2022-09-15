# hipcommerce

> 자바로 간단한 쇼핑몰 API를 구현해보았습니다.

<br>

## 사용 기술

- `Java`
- `Spring Boot 2.5.8`
- `MySQL 8.0`
- `H2`
- `JPA`
- `Querydsl`
- `Spring Security`
- `jjwt 0.11.2`
- `Redis`
- `docker-compose`
- `OpenAPI`
- `hateoas`
- `Gradle`

<br>

## 실행

- docker-compose 컨테이너 생성

```angular2html
docker compose up -d
```

<br>

- 관리자 Swagger 접속
    1. NorjaAdminApplication 실행
    2. `localhost:8081/swagger-ui.html` 접속

<br>

- 사용자 Swagger 접속
    1. NorjaApplication 실행
    2. `localhost:8081/swagger-ui.html` 접속
    3. 회원가입 -> 로그인 후, `accessToken` 값 상단의 `Authorize`에 입력

```json
{
  "token": {
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJoaXBjb21tZXJjZSIsInN1YiI6ImNvbS5oaXBjb21tZXJjZS5jb25maWcuc2VjdXJpdHkubW9kZWwuQ3JlZGVudGlhbEA3ZmMxMDFkNCIsImF1ZCI6InVzZXIiLCJleHAiOjE2NjM4MTA4NTEsImlhdCI6MTY2MzIwNjA1MSwianRpIjoiYjA2ODcxNmUtOTAyZS00MDNkLTgyYmQtNTdlM2YyZjQwYmI1Iiwic2NvcGUiOiJhY2Nlc3MiLCJ1c2VyIjoie1widXNlcklkXCI6MSxcInVzZXJuYW1lXCI6XCJzaHl1bmVAa25vdS5hYy5rclwiLFwiZW1haWxcIjpcInNoeXVuZUBrbm91LmFjLmtyXCIsXCJtb2JpbGVQaG9uZVwiOlwiMDEwMTIzNDEyMzRcIixcIm5hbWVcIjpcIuq5gOyEse2YhFwiLFwiZ2VuZGVyXCI6XCJNQUxFXCIsXCJzdGF0dXNcIjpcIkFDVElWRVwiLFwiY3JlYXRlZERhdGVcIjpcIjIwMjItMDktMTVUMTA6Mzc6MzUuODAzMzVcIixcImxhc3RNb2RpZmllZERhdGVcIjpcIjIwMjItMDktMTVUMTA6Mzc6MzUuODAzMzVcIixcImF1dGhvcml0aWVzXCI6W1wiUk9MRV9BRE1JTlwiLFwiUk9MRV9VU0VSXCJdfSJ9.MqcgFeYQ_U7P1l0MkfvYJyB_e-WZxC3cw_GTZkUTjOI"
  }
}
```

<br>

## 구조

<img width="1369" alt="스크린샷 2022-08-24 오후 10 51 49" src="https://user-images.githubusercontent.com/69466533/186436269-1289ecfe-96c2-4550-8827-3a254604fef1.png">

<br>

## 기능

### Category (카테고리)

- 관리자

| <div style="width:200px"> Method | <div style="width:300px">           URI | <div style="width:290px"> Description |
|:--------------------------------:|:---------------------------------------:|:-------------------------------------:|
|               GET                |            /api/v1/products             |             카테고리 조회 및 검색              |
|               GET                |            /api/v1/products             |              카테고리 전체 조회               |
|               GET                |          /api/v1/products/{id}          |              자녀 카테고리 조회               |
|               POST               |          /api/v1/products/{id}          |                카테고리 등록                |
|               POST               |          /api/v1/products/{id}          |              자녀 카테고리 등록               |
|               GET                |         /api/v1/products/caches         |              카테고리 상세 조회               |
|               PUT                |         /api/v1/products/caches         |                카테고리 수정                |
|              DELETE              |         /api/v1/products/caches         |                카테고리 삭제                |
|              DELETE              |         /api/v1/products/caches         |              카테고리 캐시 초기화              |

<br>

- 사용자

| <div style="width:200px"> Method | <div style="width:300px">           URI | <div style="width:290px"> Description |
|:---------------------------------------:|:---------------------------------------:|:-------------------------------------:|
|               GET                |            api/v1/categories            |              카테고리 전체 조회               |
|               GET                |     api/v1/categories/{id}/children     |              자녀 카테고리 조회               |
|               GET                |         api/v1/categories/{id}          |              카테고리 상세 조회               |

<br>

### Product (상품)

- 관리자

| <div style="width:200px"> Method | <div style="width:300px">           URI | <div style="width:290px"> Description |
|:--------------------------------:|:---------------------------------------:|:-------------------------------------:|
|               GET                |            /api/v1/products             |              상품 조회 및 검색               |
|               POST               |            /api/v1/products             |                 상품 등록                 |
|               GET                |          /api/v1/products/{id}          |               상품 상세 조회                |
|               PUT                |          /api/v1/products/{id}          |                 상품 수정                 |
|              DELETE              |          /api/v1/products/{id}          |                 상품 삭제                 |
|           DELETE            |         /api/v1/products/caches         |               상품 캐시 초기화               |

<br>

- 사용자

| <div style="width:200px"> Method | <div style="width:300px">           URI | <div style="width:290px"> Description |
|:---------------------------------------:|:-------------------------------------:|:------------------------------:|
|               GET                |            /api/v1/products             |              상품 조회 및 검색          |
|               GET                |          /api/v1/products/{id}          |               상품 상세 조회            |

<br>

### Order (주문)

- 관리자

| <div style="width:200px"> Method |  <div style="width:300px">           URI   | <div style="width:290px"> Description |
|:--------------------------------:|:------------------------------------------:|:-------------------------------------:|
|               GET                | /api/v1/orders/ordering-products/checkout  |              주문 조회 및 검색               |
|               GET                | /api/v1/orders/order-sheets/{orderSheetId} |               주문 상세 조회                |
|               PUT                |               /api/v1/orders               |               주문 상태 변경                |
|               PUT                |            /api/v1/orders/{id}             |                배송지 변경                 |

<br>

- 사용자

| <div style="width:200px"> Method |  <div style="width:300px">           URI   | <div style="width:290px"> Description |
|:--------------------------------:|:------------------------------------------:|:-------------------------------------:|
|               POST               | /api/v1/orders/ordering-products/checkout  |                주문서 생성                 |
|               GET                | /api/v1/orders/order-sheets/{orderSheetId} |                주문서 조회                 |
|               POST               |               /api/v1/orders               |                  주문                   |
|               GET                |            /api/v1/orders/{id}             |               주문 상세 조회                |
|               PUT                |        /api/v1/orders/{id}/delivery        |                배송지 변경                 |
|               PUT                |      /api/v1/orders/purchase-complete      |                 구매확정                  |
|               PUT                |       /api/v1/orders/cancel-request        |               주문 취소 요청                |
|               PUT                |       /api/v1/orders/return-request        |               주문 반품 요청                |

<br>

### Payment (결제)

- 사용자

| <div style="width:200px"> Method | <div style="width:300px">           URI | <div style="width:290px"> Description |
|:--------------------------------:|:---------------------------------------:|:-------------------------------------:|
|               POST               |        /api/v1/payments/request         |                  결제                   |
|               GET                |          /api/v1/payments/{id}          |              결제 내역 상세 조회              |

<br>

### Member (회원)

- 관리자

| <div style="width:200px"> Method | <div style="width:300px">           URI | <div style="width:290px"> Description |
|:--------------------------------:|:---------------------------------------:|:-------------------------------------:|
|               POST               |          /api/v1/members/auth           |                  로그인                  |
|               GET                |             /api/v1/members             |              회원 조회 및 검색               |
|               GET                |          /api/v1/members/{id}           |               회원 상세 조회                |

<br>

- 사용자

| <div style="width:200px"> Method | <div style="width:300px">           URI | <div style="width:290px"> Description |
|:--------------------------------:|:---------------------------------------:|:-------------------------------------:|
|               POST               |          /api/v1/members/auth           |                  로그인                  |
|               POST               |             /api/v1/members             |                 회원가입                  |
|               POST               |  /api/v1/members/{id}/change-password   |                비밀번호 변경                |
|               PUT                |          /api/v1/members/{id}           |                회원정보 변경                |
|              DELETE              |          /api/v1/members/{id}           |                 회원 탈퇴                 |

