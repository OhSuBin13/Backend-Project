# Weather API

Node.js, Express, Redis를 사용해 도시별 날씨 정보를 조회하고 캐싱하는 간단한 백엔드 API입니다.  
요청이 처음 들어오면 외부 날씨 API에서 데이터를 가져오고, 이후에는 Redis 캐시에서 같은 도시의 데이터를 우선 조회합니다.

## Features

- Express 기반 REST API
- Redis 캐시를 이용한 응답 재사용
- `express-rate-limit`을 사용한 기본 요청 제한
- `dotenv`를 통한 환경변수 관리

## Tech Stack

- Node.js
- Express
- Redis
- Axios
- dotenv

## Project Structure

```text
weather_api/
├─ config/
│  └─ redisClient.js
├─ controllers/
│  └─ weatherController.js
├─ model/
│  └─ weatherModel.js
├─ routes/
│  └─ weatherRoutes.js
├─ views/
│  └─ responseView.js
├─ .env
├─ package.json
└─ server.js
```

## Requirements

- Node.js 18+
- Redis server
- Visual Crossing Weather API key

## Installation

```bash
npm install
```

## Environment Variables

프로젝트 루트에 `.env` 파일을 만들고 아래 값을 설정합니다.

```env
PORT=3000
WEATHER_API_KEY=your_visual_crossing_api_key
REDIS_HOST=127.0.0.1 || {REDIS IP}
REDIS_PORT=6379 || {REDIS PORT}
```

설명:

- `PORT`: Express 서버 포트
- `WEATHER_API_KEY`: Visual Crossing Weather API 키
- `REDIS_HOST`: Redis 서버 호스트
- `REDIS_PORT`: Redis 서버 포트

현재 코드에서는 `REDIS_HOST`, `REDIS_PORT`를 생략하면 기본값으로 `127.0.0.1:6379`를 사용합니다.

## Running Redis

이 프로젝트는 서버 시작 시 Redis 연결을 시도합니다.  
따라서 `node server.js`를 실행하기 전에 Redis 서버가 먼저 실행 중이어야 합니다.

예시:

```bash
redis-server
```

## Run the Server

```bash
node server.js
```

정상 실행 시 콘솔 예시:

```text
Redis Connected
Server is running on port 3000
```

## API Endpoint

### Get Weather by City

```http
GET /weather/:city
```

## Response Example

외부 API에서 처음 가져온 경우:

```json
{
  "source": "api",
  "data": {
    "queryCost": 1,
    "latitude": 37.566,
    "longitude": 126.9784
  }
}
```

Redis 캐시에서 조회된 경우:

```json
{
  "source": "cache",
  "data": {
    "queryCost": 1,
    "latitude": 37.566,
    "longitude": 126.9784
  }
}
```

에러 응답 예시:

```json
{
  "error": "Failed to get weather data"
}
```

## Request Flow

1. 클라이언트가 `/weather/:city`로 요청을 보냅니다.
2. 컨트롤러가 도시 이름을 받아 모델 레이어에 전달합니다.
3. 모델이 Redis에서 먼저 캐시 데이터를 조회합니다.
4. 캐시가 있으면 캐시 데이터를 반환합니다.
5. 캐시가 없으면 외부 날씨 API를 호출합니다.
6. 받은 데이터를 Redis에 저장한 뒤 응답합니다.

## Rate Limiting

현재 서버에는 모든 요청에 대해 아래 제한이 적용됩니다.

- 15분 동안 IP당 최대 100회 요청

설정 위치: `server.js`

## Notes

- 현재 `weatherModel.js`는 모듈이 로드될 때 즉시 Redis 연결을 시도합니다.
- Redis가 실행 중이지 않으면 서버 시작 단계에서 연결 오류가 발생할 수 있습니다.
- 현재 캐시 저장 시간은 1시간(`3600초`)입니다.

## Dependencies

주요 패키지:

- `express`
- `axios`
- `redis`
- `dotenv`
- `express-rate-limit`
- `nodemon`

## Future Improvements

- `npm run dev`용 개발 스크립트 추가
- 에러 응답 구조 개선
- 입력값 검증 강화
- 테스트 코드 추가
- Redis 연결 초기화 위치 개선
