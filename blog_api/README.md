# Blog API

Spring Boot 기반의 간단한 블로그 게시글 관리 API 프로젝트입니다.  
게시글 생성, 조회, 수정, 삭제 기능과 키워드 검색 기능을 제공합니다.

## 기술 스택

- Java 17
- Spring Boot 4.0.5
- MySQL
- Gradle

## 주요 기능

- 게시글 생성
- 전체 게시글 조회
- 게시글 수정
- 게시글 삭제
- 키워드 기반 게시글 검색
- 간단한 메모리 캐시 적용

## 프로젝트 구조

```text
src/main/java/com/example/blog_api
├── config
│   ├── CacheConfig.java
│   └── WebConfig.java
├── controller
│   └── BlogController.java
├── model
│   └── Blog.java
├── repository
│   └── BlogRepository.java
└── services
    └── BlogServices.java
```

## 실행 환경

다음 환경이 준비되어 있어야 합니다.

- JDK 17
- MySQL 8.x 이상 권장
- Gradle Wrapper 사용 가능 환경

## 설정 정보

기본 애플리케이션 설정은 [`application.properties`](C:/Users/dhtnq/Backend-Project/blog_api/src/main/resources/application.properties:1), 개발 환경 DB 설정은 [`application-dev.properties`](C:/Users/dhtnq/Backend-Project/blog_api/src/main/resources/application-dev.properties:1)에 있습니다.

기본 설정값:

- 서버 포트: `8080`
- 활성 프로필: `dev`
- 데이터베이스: `blogging`

현재 개발용 DB 설정 예시는 아래와 같습니다.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/blogging?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=1234
```

실행 전 로컬 환경에 맞게 사용자명, 비밀번호를 변경하는 것을 권장합니다.

## 실행 방법

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd blog_api
```

### 2. MySQL 실행

로컬 MySQL 서버가 실행 중인지 확인합니다.

### 3. 애플리케이션 실행

Windows:

```bash
gradlew.bat bootRun
```


정상 실행되면 기본 주소는 아래와 같습니다.

```text
http://localhost:8080
```


## API 엔드포인트

컨트롤러는 [`BlogController.java`](C:/Users/dhtnq/Backend-Project/blog_api/src/main/java/com/example/blog_api/controller/BlogController.java:1)에 정의되어 있습니다.

| Method | Endpoint | 설명 |
|---|---|---|
| `POST` | `/posts` | 게시글 생성 |
| `GET` | `/posts` | 전체 게시글 조회 |
| `PUT` | `/posts/{id}` | 게시글 수정 |
| `DELETE` | `/posts/{id}` | 게시글 삭제 |
| `GET` | `/posts/search?term={keyword}` | 키워드 검색 |

