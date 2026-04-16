# Todo List API Handoff

Last updated: 2026-04-16

## Project goal

Build a RESTful Todo List API with:

- user registration and login
- token-based authentication
- todo CRUD
- authorization so users can only modify their own todos
- validation, pagination, filtering, and proper error handling

## Work completed

### 1. Dependency direction

`build.gradle` has been adjusted toward the project requirements.

Current notable dependencies:

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `jjwt`
- `postgresql`
- `h2` for tests
- `spring-boot-starter-test`
- `spring-boot-starter-security-test`

Notes:

- Earlier `spring-boot-starter-webmvc` was replaced with `spring-boot-starter-web`.
- `compileJava` succeeds with the current setup.

### 2. Package structure created

Base package:

- `com.example.todolist`

Main package layout in place:

- `auth`
- `common`
- `todo`
- `user`

### 3. Entities implemented

#### `User`

File:

- `src/main/java/com/example/todolist/user/entity/User.java`

Implemented fields:

- `id`
- `name`
- `email` with unique constraint
- `password`
- `todos` one-to-many relation

Implemented methods:

- constructor with `name`, `email`, `password`
- `updateName`
- `updatePassword`
- `addTodo`

#### `Todo`

File:

- `src/main/java/com/example/todolist/todo/entity/Todo.java`

Implemented fields:

- `id`
- `title`
- `description`
- `user` many-to-one relation

Implemented methods:

- constructor with `title`, `description`, `user`
- `update`

### 4. Repositories implemented

#### `UserRepository`

File:

- `src/main/java/com/example/todolist/user/repository/UserRepository.java`

Methods added:

- `existsByEmail(String email)`
- `Optional<User> findByEmail(String email)`

#### `ToDoRepository`

File:

- `src/main/java/com/example/todolist/todo/repository/ToDoRepository.java`

Methods added:

- `Page<Todo> findByUserId(Long userId, Pageable pageable)`
- `Optional<Todo> findByIdAndUserId(Long id, Long userId)`

### 5. Auth DTOs implemented

Files:

- `src/main/java/com/example/todolist/auth/dto/RegisterRequest.java`
- `src/main/java/com/example/todolist/auth/dto/LoginRequest.java`
- `src/main/java/com/example/todolist/auth/dto/TokenResponse.java`

Notes:

- `RegisterRequest` and `LoginRequest` use validation annotations.
- DTOs are implemented as Java `record`s.

### 6. Auth service implemented

File:

- `src/main/java/com/example/todolist/auth/service/AuthService.java`

Implemented behavior:

- register:
  - checks duplicate email
  - hashes password with `PasswordEncoder`
  - saves user
  - returns token
- login:
  - finds user by email
  - checks password
  - returns token

Token behavior:

- JWT generation is implemented directly inside `AuthService`
- reads:
  - `jwt.secret`
  - `jwt.expiration-hours`
- has a fallback default secret for development

Important note:

- `compileJava` succeeds, but the service currently uses a deprecated JJWT API according to Gradle warnings
- this is not blocking compile, but the next agent may want to modernize token generation

### 7. Security config implemented

File:

- `src/main/java/com/example/todolist/common/config/SecurityConfig.java`

Implemented behavior:

- permits:
  - `POST /register`
  - `POST /login`
- requires authentication for all other routes
- disables:
  - csrf
  - form login
  - http basic
  - logout
- uses stateless session policy
- registers `BCryptPasswordEncoder`
- returns JSON-like strings for:
  - `401 Unauthorized`
  - `403 Forbidden`

Important limitation:

- JWT validation filter is not implemented yet
- this means tokens can be issued, but incoming bearer tokens are not yet parsed and converted into authenticated Spring Security context
- practically, `/register` and `/login` are usable, but authenticated todo endpoints are not yet wired end-to-end

### 8. Auth controller implemented

File:

- `src/main/java/com/example/todolist/auth/controller/AuthController.java`

Implemented endpoints:

- `POST /register`
- `POST /login`

Notes:

- request DTOs use `@Valid`
- register currently returns `201 Created`

## Current gaps / incomplete work

The following core pieces are still missing:

- JWT parsing/validation component
- JWT authentication filter
- security filter integration for bearer tokens
- custom authenticated principal handling
- todo DTOs:
  - create request
  - update request
  - response
  - paged response
- todo service
- todo controller
- global exception handler
- user-facing error response unification
- datasource configuration in `application.properties`
- JPA properties and database connection settings
- tests

## Known issues and cautions

### 1. Runtime authentication is incomplete

Security is configured to require authentication for most routes, but there is no JWT request filter yet. Do not assume `/todos` can work with bearer tokens yet.

### 2. Database runtime config is missing

`src/main/resources/application.properties` currently only contains:

- `spring.application.name=ToDoList`

So database URL, username, password, JPA settings, and JWT config still need to be added.

### 3. Tests are not in a finished state

Earlier test execution failed because datasource configuration was missing. `compileJava` succeeds, but the project is not yet test-complete or runtime-complete.

### 4. Repository naming

The repository class is named `ToDoRepository`, while the entity is `Todo`.

This is not a compile problem, but the next agent may want to rename it to `TodoRepository` for naming consistency before more code depends on it.

## Recommended next steps

Recommended implementation order:

1. Add JWT token provider or extract JWT logic from `AuthService`
2. Implement JWT authentication filter
3. Connect filter into `SecurityConfig`
4. Add custom user details or principal resolution strategy
5. Add todo DTOs
6. Add todo service
7. Add todo controller
8. Add global exception handler
9. Add `application.properties` DB and JWT settings
10. Add integration tests for auth and todo APIs

## Suggested runtime configuration to add next

Likely needed in `application.properties` soon:

- datasource URL
- datasource username
- datasource password
- JPA ddl-auto strategy
- SQL logging option
- JWT secret
- JWT expiration hours

## Verification completed so far

Verified successfully:

- `gradlew.bat compileJava`

Not yet verified end-to-end:

- app startup with real datasource
- auth endpoint HTTP calls
- JWT-authenticated todo calls
- tests passing

## Files most relevant for the next agent

- `src/main/java/com/example/todolist/common/config/SecurityConfig.java`
- `src/main/java/com/example/todolist/auth/controller/AuthController.java`
- `src/main/java/com/example/todolist/auth/service/AuthService.java`
- `src/main/java/com/example/todolist/auth/dto/RegisterRequest.java`
- `src/main/java/com/example/todolist/auth/dto/LoginRequest.java`
- `src/main/java/com/example/todolist/auth/dto/TokenResponse.java`
- `src/main/java/com/example/todolist/user/entity/User.java`
- `src/main/java/com/example/todolist/user/repository/UserRepository.java`
- `src/main/java/com/example/todolist/todo/entity/Todo.java`
- `src/main/java/com/example/todolist/todo/repository/ToDoRepository.java`

