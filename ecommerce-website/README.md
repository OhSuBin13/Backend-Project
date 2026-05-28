# ReSellMart - E-commerce Website

ReSellMart는 중고 상품을 등록하고 구매할 수 있는 풀스택 이커머스 웹 애플리케이션입니다. 사용자는 상품을 판매자로 등록하고, 다른 사용자의 상품을 장바구니나 위시리스트에 담은 뒤 Stripe Checkout을 통해 결제할 수 있습니다. 관리자는 사용자, 상품, 카테고리와 주요 통계를 관리할 수 있습니다.

## 주요 기능

- 이메일 인증 기반 회원가입 및 로그인
- JWT access token과 HttpOnly refresh token 기반 인증
- 선택형 TOTP MFA 인증
- 상품 등록, 조회, 검색, 수정, 판매 상태 변경
- 상품 이미지 업로드 및 대표 이미지 조회
- 카테고리 생성, 수정, 삭제, 검색
- 장바구니, 위시리스트 관리
- 배송지/청구지 주소 관리 및 기본 주소 설정
- Stripe Checkout 기반 주문 생성
- 구매 내역, 판매 내역, 배송/수령 상태 관리
- 관리자 대시보드, 사용자/상품/주문 통계

## 기술 스택

### Backend

- Java 17
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Mail, Thymeleaf
- MySQL
- JWT (`jjwt`)
- TOTP MFA (`dev.samstevens.totp`)
- Stripe Java SDK
- Gradle
- JUnit 5, Testcontainers

### Frontend

- React 19
- TypeScript
- Vite 6
- React Router
- TanStack React Query
- Axios
- Mantine UI
- Mantine React Table
- Zod

## 프로젝트 구조

```text
ecommerce-website/
├── backend/        # Spring Boot REST API
└── frontend/       # React + Vite client
```

## 사전 준비

- Java 17
- Node.js 18 이상
- npm
- MySQL 8.x
- 로컬 SMTP 서버 또는 테스트용 메일 서버
  - 예: MailHog, Mailpit
- Stripe 계정 및 Secret Key
- 백엔드 테스트 실행 시 Docker
  - Testcontainers가 MySQL 컨테이너를 사용합니다.

## 환경 설정

### Backend

백엔드 설정 파일은 `backend/src/main/resources/application-dev.yml`입니다.

기본 개발 설정은 다음 값을 사용합니다.

```yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/resellmart_db
    username: root
    password: 1234

application:
  frontend:
    base-url: "http://localhost:5173"
```

로컬 환경에 맞게 MySQL 접속 정보를 수정하거나, 동일한 DB와 계정을 준비합니다.

필수 환경변수:

```bash
MAIL_USERNAME=your-mail-username
MAIL_PASSWORD=your-mail-password
JWT_SECRET_KEY=your-base64-jwt-secret
STRIPE_SECRET_KEY=sk_test_...
```

Windows PowerShell 예시:

```powershell
$env:MAIL_USERNAME="user"
$env:MAIL_PASSWORD="password"
$env:JWT_SECRET_KEY="your-base64-jwt-secret"
$env:STRIPE_SECRET_KEY="sk_test_..."
```

현재 개발 설정은 `spring.jpa.hibernate.ddl-auto: none`입니다. 저장소에 DB 마이그레이션 파일이 포함되어 있지 않으므로, 로컬 실행 전 `resellmart_db` 스키마와 필요한 테이블/초기 데이터를 준비해야 합니다. 빠른 로컬 확인용으로만 `ddl-auto`를 `update` 또는 `create`로 바꿀 수 있지만, 실제 개발/운영에서는 명시적인 마이그레이션 도구 사용을 권장합니다.

### Frontend

`frontend` 디렉터리에 `.env` 파일을 만들고 백엔드 API 주소를 설정합니다.

```env
VITE_API_URL=http://localhost:8080
```

## 실행 방법

저장소를 클론한 뒤 프로젝트 디렉터리로 이동합니다.

```bash
git clone https://github.com/OhSuBin13/Backend-Project.git
cd Backend-Project/ecommerce-website
```

### Backend 실행

Windows:

```powershell
cd backend
.\gradlew.bat bootRun
```

macOS/Linux:

```bash
cd backend
./gradlew bootRun
```

백엔드는 기본적으로 `http://localhost:8080`에서 실행됩니다.

### Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 기본적으로 `http://localhost:5173`에서 실행됩니다.

## 주요 API

| 도메인 | Endpoint |
| --- | --- |
| 인증 | `POST /api/auth/registration`, `POST /api/auth/login`, `POST /api/auth/activation`, `POST /api/auth/refresh`, `POST /api/auth/verification` |
| 사용자 | `GET /api/users/me`, `GET /api/users`, `PUT /api/users/{user-id}`, `PUT /api/users/{user-id}/image` |
| 상품 | `GET /api/products`, `GET /api/products/latest`, `GET /api/products/{product-id}`, `POST /api/products`, `PATCH /api/products/{product-id}` |
| 상품 이미지 | `PUT /api/products/{product-id}/images`, `GET /api/products/{product-id}/images/primary` |
| 카테고리 | `GET /api/categories`, `GET /api/categories/parents`, `POST /api/categories`, `PUT /api/categories/{category-id}`, `DELETE /api/categories/{category-id}` |
| 장바구니 | `GET /api/users/{user-id}/cart/products`, `POST /api/users/{user-id}/cart/products`, `PATCH /api/users/{user-id}/cart/products/{product-id}`, `DELETE /api/users/{user-id}/cart/products/{product-id}` |
| 위시리스트 | `GET /api/users/{user-id}/wishlist/products`, `POST /api/users/{user-id}/wishlist/products`, `DELETE /api/users/{user-id}/wishlist/products/{product-id}` |
| 주소 | `GET /api/users/{user-id}/addresses`, `POST /api/addresses`, `PUT /api/addresses/{address-id}`, `PATCH /api/addresses/{address-id}/main`, `DELETE /api/addresses/{address-id}` |
| 주문 | `POST /api/orders`, `GET /api/users/{user-id}/purchases`, `GET /api/users/{user-id}/sales`, `PATCH /api/orders/{order-id}/products/{product-id}/ship`, `PATCH /api/orders/{order-id}/products/{product-id}/deliver` |
| Stripe | `POST /api/orders/stripe-webhook`, `POST /api/orders/fulfill` |
| 통계 | `GET /api/users/statistics`, `GET /api/products/statistics`, `GET /api/orders/statistics` |

## 테스트 및 빌드

### Backend

```bash
cd backend
./gradlew test
./gradlew build
```

Windows에서는 `./gradlew` 대신 `.\gradlew.bat`을 사용합니다.

### Frontend

```bash
cd frontend
npm run lint
npm run build
```

## 참고 사항

- 상품/사용자/주문 이미지 파일은 백엔드 설정의 `application.file.upload.*` 경로에 저장됩니다.
- Stripe webhook을 로컬에서 테스트하려면 Stripe CLI나 ngrok 같은 터널링 도구를 사용하고, `application.stripe.webhook-secret` 및 `application.backend.base-https-url` 값을 로컬 환경에 맞게 수정합니다.
- refresh token은 HttpOnly cookie로 내려가며, 프론트엔드 Axios 클라이언트는 `withCredentials: true`로 요청합니다.
- 운영 환경에서는 DB 계정, JWT secret, Stripe key, SMTP 계정 같은 민감 정보를 설정 파일에 직접 커밋하지 말고 환경변수 또는 secret manager로 관리하세요.
