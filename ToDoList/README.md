# ToDoList API

JWT 기반 인증을 사용하는 Todo List REST API 프로젝트입니다.

이 프로젝트는 다음 기능을 제공합니다.

- JWT 인증
- 현재 로그인한 사용자 정보 조회
- Todo 생성, 조회, 수정, 삭제
- 키워드 기반 Todo 검색

## 기술 스택

- Java 17
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security
- PostgreSQL
- JJWT
- Gradle

## 프로젝트 구조

```text
src
|-- main
|   |-- java/com/example/todolist
|   |   |-- auth
|   |   |-- common
|   |   |-- todo
|   |   `-- user
|   `-- resources
|       `-- application.properties
`-- test
    |-- java/com/example/todolist
    `-- resources/application.properties
```

## 실행 환경

- JDK 17 이상
- PostgreSQL

## 로컬 실행 방법

### 1. 데이터베이스 생성

예시:

```sql
CREATE DATABASE todolist;
```

### 2. 애플리케이션 실행



```bash
gradlew.bat bootRun
```


## 테스트 실행


```bash
gradlew.bat test
```


테스트는 `src/test/resources/application.properties`에 설정된 H2 인메모리 데이터베이스를 사용합니다.

## API 개요

### 인증 API

| Method | Path | 설명 | 인증 필요 여부 |
| --- | --- | --- | --- |
| `POST` | `/register` | 회원가입 후 JWT 발급 | 아니오 |
| `POST` | `/login` | 로그인 후 JWT 발급 | 아니오 |

### 사용자 API

| Method | Path | 설명 | 인증 필요 여부 |
| --- | --- | --- | --- |
| `GET` | `/me` | 현재 로그인한 사용자 정보 조회 | 예 |

### Todo API

| Method | Path | 설명 | 인증 필요 여부 |
| --- | --- | --- | --- |
| `POST` | `/todos` | Todo 생성 | 예 |
| `GET` | `/todos` | Todo 목록 조회 | 예 |
| `GET` | `/todos/{todoId}` | Todo 단건 조회 | 예 |
| `PUT` | `/todos/{todoId}` | Todo 수정 | 예 |
| `DELETE` | `/todos/{todoId}` | Todo 삭제 | 예 |

