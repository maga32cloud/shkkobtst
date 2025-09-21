# SH Cafe Mobile Application

카페 모바일 앱의 백엔드 서비스입니다. Spring Boot 기반으로 개발되었으며, 회원 관리, 상품 관리, 주문 및 결제 기능을 제공합니다.

## 🏗️ 시스템 아키텍처

### 기술 스택
- **Backend**: Spring Boot 3.5.5
- **Database**: MySQL 8.0
- **Security**: JWT Authentication
- **JPA**: JWT Authentication
- **Build Tool**: Gradle

### 주요 기능
- 회원 관리 (가입, 로그인, 탈퇴)
- 상품 목록확인, 상품 주문
- 결제 및 취소 처리

## 📊 ERD
![image](https://github.com/maga32cloud/shkkobtst/blob/description/ERD.png)

### 데이터베이스 상세구조

#### 1. category (카테고리)
| 컬럼명 | 타입 | 제약조건 | 코멘트 |
|--------|------|----------|--------|
| category_id | bigint | PK, AUTO_INCREMENT | 카테고리ID |
| category_name | varchar(45) | NOT NULL | 카테고리명 |
| display_order | int | - | 표시순서 |
| use_yn | varchar(1) | NOT NULL | 사용여부 |
| reg_dtm | datetime | NOT NULL | 등록일시 |

#### 2. member (회원)
| 컬럼명 | 타입 | 제약조건 | 코멘트 |
|--------|------|----------|--------|
| member_id | varchar(20) | PK | 회원ID |
| password | varchar(65) | NOT NULL | 비밀번호 |
| email | varchar(45) | NOT NULL | 이메일 |
| name | varchar(10) | NOT NULL | 이름 |
| tel_no | varchar(12) | NOT NULL | 전화번호 |
| gender | varchar(1) | NOT NULL | 성별 |
| birth_dt | date | NOT NULL | 생년월일 |
| member_sts_cd | varchar(2) | NOT NULL | 회원상태코드 |
| reg_dt | date | NOT NULL | 등록일 |
| cls_dt | date | - | 탈퇴일 |

#### 3. product (상품)
| 컬럼명 | 타입 | 제약조건 | 코멘트 |
|--------|------|----------|--------|
| product_id | bigint | PK, AUTO_INCREMENT | 상품ID |
| category_id | bigint | FK → category.category_id | 카테고리ID |
| product_name | varchar(45) | NOT NULL | 상품명 |
| description | varchar(1000) | - | 상품설명 |
| price | int | NOT NULL | 가격 |
| display_order | int | - | 표시순서 |
| img_url | varchar(300) | - | 이미지url |
| use_yn | varchar(1) | NOT NULL | 사용여부 |
| mod_dtm | datetime | NOT NULL | 수정일시 |
| reg_dtm | datetime | NOT NULL | 등록일시 |

#### 4. product_history (상품이력)
| 컬럼명 | 타입 | 제약조건 | 코멘트 |
|--------|------|----------|--------|
| history_id | bigint | PK, AUTO_INCREMENT | 이력ID |
| product_id | bigint | FK → product.product_id | 상품ID |
| category_id | bigint | NOT NULL | 카테고리ID |
| product_name | varchar(45) | NOT NULL | 상품명 |
| description | varchar(1000) | - | 상품설명 |
| price | int | NOT NULL | 가격 |
| display_order | int | - | 표시순서 |
| img_url | varchar(300) | - | 이미지URL |
| use_yn | varchar(1) | NOT NULL | 사용여부 |
| cls_dtm | datetime | - | 종료일시 |
| mod_dtm | datetime | NOT NULL | 수정일시 |
| reg_dtm | datetime | NOT NULL | 등록일시 |

#### 5. orders (주문)
| 컬럼명 | 타입 | 제약조건 | 코멘트 |
|--------|------|----------|--------|
| order_id | bigint | PK, AUTO_INCREMENT | 주문ID |
| member_id | varchar(20) | FK → member.member_id | 회원ID |
| pickup_no | varchar(20) | - | 픽업번호 |
| total_amount | int | NOT NULL | 총 주문 금액 |
| order_sts_cd | varchar(2) | NOT NULL | 주문상태코드 |
| mod_dtm | datetime | NOT NULL | 수정일시 |
| reg_dtm | datetime | NOT NULL | 등록일시 |

#### 6. order_item (주문상품)
| 컬럼명 | 타입 | 제약조건 | 코멘트 |
|--------|------|----------|--------|
| order_item_id | bigint | PK, AUTO_INCREMENT | 주문상품ID |
| order_id | bigint | FK → orders.order_id | 주문ID |
| product_id | bigint | FK → product.product_id | 상품ID |
| quantity | int | NOT NULL | 수량 |
| price | int | NOT NULL | 가격 |
| total_price | int | NOT NULL | 총 가격 |
| reg_dtm | datetime | NOT NULL | 등록일시 |

#### 7. payment (결제)
| 컬럼명 | 타입 | 제약조건 | 코멘트 |
|--------|------|----------|--------|
| payment_id | bigint | PK, AUTO_INCREMENT | 결제ID |
| order_id | bigint | FK → orders.order_id | 주문ID |
| member_id | varchar(20) | FK → member.member_id | 회원ID |
| idempotency_key | varchar(36) | UNIQUE | 멱등키 |
| pay_mtd_cd | varchar(2) | NOT NULL | 결제수단코드 |
| pay_amount | int | NOT NULL | 결제금액 |
| pay_sts_cd | varchar(2) | NOT NULL | 결제상태코드 |
| transaction_id | varchar(100) | - | 거래ID |
| paid_dtm | datetime | - | 결제일시 |
| cancel_dtm | datetime | - | 취소일시 |
| reg_dtm | datetime | NOT NULL | 등록일시 |

## 🚀 실행 방법

### 빌드 및 실행
```bash
$ git clone https://github.com/maga32cloud/shkkobtst.git
$ cd shkkobtst
$ ./gradlew clean build
$ java -jar build/libs/shcafe-0.0.1-SNAPSHOT.jar
```

```
swegger로 확인하기 ->  http://localhost:8080/swagger-ui/index.html
```

## 📁 프로젝트 구조

```
src/main/java/com/cafe/mobile/shcafe/
├── batch/                    # 배치 작업
├── common/                   # 공통 모듈
│   ├── config/              # 설정
│   ├── exception/           # 예외 처리
│   ├── jwt/                 # JWT 인증
│   ├── model/               # 공통 모델
│   ├── type/                # 상수 정의
│   └── util/                # 유틸리티
├── member/                  # 회원 관리
├── order/                   # 주문 관리
├── payment/                 # 결제 관리
└── product/                 # 상품 관리
```

## 🔧 주요 기능

### 회원 관리
- 회원 가입/로그인
- JWT 기반 인증
- 회원 정보 관리

### 상품 관리
- 카테고리별 상품 조회
- 상품 상세 정보
- 상품 이력 관리

### 주문 관리
- 주문 생성 및 관리
- 주문 상태 추적
- 픽업 번호 생성

### 결제 관리
- 다양한 결제 수단 지원
- 결제 상태 관리
- 결제 취소 처리

## 📝 API 문서

![image](https://github.com/maga32cloud/shkkobtst/blob/description/API.png)

[>> PDF 파일로 보기 <<](https://github.com/maga32cloud/shkkobtst/blob/description/SH%20Cafe%20API.pdf)

서버 실행 후 swagger를 통해 상세한 문서와 테스트를 하실 수 있습니다.
```
기본 접속url : http://localhost:8080/swagger-ui/index.html
```

- 기본적으로 로그인 해야 사용할 수 있는 기능과 로그인 없이 사용 가능한 기능이 있습니다.
- 회원 api로 가입 후, 로그인 api로 ID와 비밀번호를 입력하시면 jwt토큰이 authorization: 헤더로 내려옵니다 (아래와 같은 형식)
```
eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6Im1hZ2EzMiIsImlhdCI6MTc1ODQ0NzY2MSwiZXhwIjoxNzU4NDUxMjYxfQ.izupHqBVATgHMMZ8TleSY_KEd_AuOix_ZsaXoYisVjI 
```
- 해당 내용을 복사하여 맨 위의 Authorize 부분에 입력하시면 따로 헤더 설정하실 필요 없이 기능들을 이용하실 수 있습니다.

각 컨트롤러별 API 엔드포인트는 다음과 같습니다:

- **MemberController**: 회원 관련 API
- **ProductController**: 상품 관련 API  
- **OrderController**: 주문 관련 API
- **PaymentController**: 결제 관련 API
