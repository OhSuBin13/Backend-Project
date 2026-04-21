# URL Shortening

Spring Boot 기반의 간단한 URL 단축 서비스입니다.  
긴 URL을 저장하고 짧은 코드로 조회, 수정, 삭제할 수 있으며, 통계 조회를 통해 접근 횟수도 확인할 수 있습니다.

## 주요 기능

- 긴 URL을 저장하고 고유한 `shortCode`를 생성합니다.
- `shortCode`로 저장된 URL 정보를 조회합니다.
- 기존 URL 정보를 수정합니다.
- 저장된 URL 정보를 삭제합니다.
- 통계 조회 API를 통해 접근 횟수(`accessCount`)를 확인합니다.
- 존재하지 않는 단축 코드 요청에 대해 `404 Not Found`를 반환합니다.
- 잘못된 요청 본문 또는 빈 URL 입력에 대해 `400 Bad Request`를 반환합니다.

## 기술 스택

- Java 17
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- H2 Database
- Gradle
- Lombok

## 프로젝트 구조

```text
src
├─ main
│  ├─ java/com/example/url_shortening
│  │  ├─ controller
│  │  ├─ dto
│  │  ├─ entity
│  │  ├─ exception
│  │  ├─ mapper
│  │  ├─ repository
│  │  └─ service
│  └─ resources
│     ├─ application.properties
│     ├─ application-dev.properties
│     ├─ application-prod.properties
│     └─ schema-prod.sql
└─ test
   └─ java/com/example/url_shortening
```

## 실행 환경

실행:

```powershell
.\gradlew.bat bootRun
```

테스트 실행:

```powershell
.\gradlew.bat test
```

## API 명세

기본 경로는 `/shorten` 입니다.

### 1. 단축 URL 생성

`POST /shorten`

요청 본문:

```json
{
  "url": "https://example.com/some/very/long/path"
}
```

응답 예시: `201 Created`

```json
{
  "id": 1,
  "url": "https://example.com/some/very/long/path",
  "shortCode": "aB12x",
  "createdDate": "2026-04-21T10:00:00",
  "updatedDate": "2026-04-21T10:00:00"
}
```

잘못된 요청:

- 요청 본문이 비어 있으면 `400 Bad Request`
- `url`이 빈 문자열 또는 공백 문자열이면 `400 Bad Request`

### 2. 단축 URL 조회

`GET /shorten/{shortCode}`

응답 예시: `200 OK`

```json
{
  "id": 1,
  "url": "https://example.com/some/very/long/path",
  "shortCode": "aB12x",
  "createdDate": "2026-04-21T10:00:00",
  "updatedDate": "2026-04-21T10:00:00"
}
```

동작 특이사항:

- 이 API는 조회 후 접근 횟수(`accessCount`)를 1 증가시킵니다.

오류 응답:

- 존재하지 않는 `shortCode`이면 `404 Not Found`

### 3. 단축 URL 수정

`PUT /shorten/{shortCode}`

요청 본문:

```json
{
  "url": "https://example.com/updated/path"
}
```

응답 예시: `200 OK`

```json
{
  "id": 1,
  "url": "https://example.com/updated/path",
  "shortCode": "aB12x",
  "createdDate": "2026-04-21T10:00:00",
  "updatedDate": "2026-04-21T11:00:00"
}
```

오류 응답:

- 요청 본문 누락 시 `400 Bad Request`
- `url`이 `null`, 빈 문자열, 공백 문자열이면 `400 Bad Request`
- 존재하지 않는 `shortCode`이면 `404 Not Found`

### 4. 단축 URL 삭제

`DELETE /shorten/{shortCode}`

응답:

- `204 No Content`

오류 응답:

- 존재하지 않는 `shortCode`이면 `404 Not Found`

### 5. 단축 URL 통계 조회

`GET /shorten/{shortCode}/stats`

응답 예시: `200 OK`

```json
{
  "id": 1,
  "url": "https://example.com/updated/path",
  "shortCode": "aB12x",
  "createdDate": "2026-04-21T10:00:00",
  "updatedDate": "2026-04-21T11:00:00",
  "accessCount": 3
}
```

오류 응답:

- 존재하지 않는 `shortCode`이면 `404 Not Found`

## 예외 처리

현재 프로젝트는 `LinkNotFoundException`을 전역 예외 처리기로 변환하여 `404 Not Found`를 반환합니다.

관련 파일:

- [LinkExceptionHandler.java](C:/Users/subin/Backend-Project/url_shortening/src/main/java/com/example/url_shortening/controller/LinkExceptionHandler.java:1)

응답 예시:

```json
{
  "type": "about:blank",
  "title": "Link not found",
  "status": 404,
  "detail": "Link not found for shortCode: missing",
  "instance": "/shorten/missing"
}
```
