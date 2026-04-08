# Unit Converter

Spring Boot로 만든 단위 변환 웹 애플리케이션입니다.  
길이, 무게, 온도 단위를 변환할 수 있으며, 정적 프론트엔드와 REST API를 함께 제공합니다.

## 주요 기능

- 길이 변환
  - `mm`, `cm`, `m`, `km`, `in`, `ft`, `yd`, `mi`
- 무게 변환
  - `mg`, `g`, `kg`, `oz`, `lb`
- 온도 변환
  - `C`, `F`, `K`
- 브라우저에서 바로 사용할 수 있는 단순한 UI 제공
- JSON 기반 REST API 제공

## 기술 스택

- Java 17
- Spring Boot 4.0.5
- Gradle 9.4.1
- Lombok
- jQuery

## 프로젝트 구조

```text
src
├─ main
│  ├─ java/com/example/unit_converter
│  │  ├─ controllers
│  │  │  └─ ConvertController.java
│  │  ├─ dto
│  │  │  └─ RequestDto.java
│  │  ├─ services
│  │  │  └─ ConvertService.java
│  │  └─ UnitConverterApplication.java
│  └─ resources
│     ├─ application.properties
│     └─ static
│        ├─ index.html
│        ├─ script.js
│        └─ style.css
└─ test
   └─ java/com/example/unit_converter
      └─ UnitConverterApplicationTests.java
```

## 실행 방법

### 1. 터미널에서 실행

Windows:

```powershell
.\gradlew.bat bootRun
```

macOS / Linux:

```bash
./gradlew bootRun
```

실행 후 브라우저에서 아래 주소로 접속합니다.

```text
http://localhost:8080
```


## API 명세

기본 요청 형식:

```json
{
  "from": "cm",
  "to": "m",
  "value": 125
}
```

### 1. 길이 변환

- Endpoint: `POST /convert/length`

예시:

```http
POST /convert/length
Content-Type: application/json

{
  "from": "cm",
  "to": "m",
  "value": 125
}
```

응답 예시:

```json
1.25
```

### 2. 무게 변환

- Endpoint: `POST /convert/weight`

예시:

```http
POST /convert/weight
Content-Type: application/json

{
  "from": "kg",
  "to": "g",
  "value": 2
}
```

### 3. 온도 변환

- Endpoint: `POST /convert/temperature`

예시:

```http
POST /convert/temperature
Content-Type: application/json

{
  "from": "C",
  "to": "F",
  "value": 30
}
```

응답 예시:

```json
86.0
```

## 화면 사용 방법

1. 메인 페이지에서 변환할 카테고리(`Length`, `Weight`, `Temperature`)를 선택합니다.
2. 값과 변환 전 단위, 변환 후 단위를 입력합니다.
3. `Convert` 버튼을 누르면 결과가 화면 하단에 표시됩니다.

## 오류 응답

지원하지 않는 단위를 요청하면 `400 Bad Request`와 함께 아래 메시지를 반환합니다.

```text
Incorrect values
```

## 개선 아이디어

- 예외 처리 공통화
- 잘못된 입력값에 대한 상세 메시지 제공
- 단위 변환 테스트 케이스 추가
- 반응형 UI 개선
- 결과 포맷팅 개선

