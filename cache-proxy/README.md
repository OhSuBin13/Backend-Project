# Cache Proxy

`cache-proxy`는 요청을 원본 서버(origin server)로 전달하고, 응답을 메모리에 캐시하는 간단한 Go 기반 HTTP 프록시 서버입니다.

이미 캐시된 응답을 반환할 때는 `X-Cache: HIT` 헤더를 포함하고, 처음 원본 서버에서 응답을 받아 캐시에 저장하는 경우에는 `X-Cache: MISS` 헤더를 포함합니다.

## 주요 기능

- 들어온 HTTP 요청을 설정된 원본 서버로 전달
- 단순한 요청 키를 기준으로 응답을 메모리에 캐시
- 캐시 상태를 확인할 수 있도록 `X-Cache` 응답 헤더 추가
- 명령어로 메모리 캐시 초기화 지원
- Go 표준 라이브러리만 사용한 가벼운 구현

## 프로젝트 구조

```text
.
|-- cmd/
|   `-- cache-proxy/
|       `-- main.go
|-- internal/
|   |-- cache/
|   |   `-- cache.go
|   `-- proxy/
|       `-- proxy.go
|-- go.mod
`-- README.md
```

## 실행 환경

- Go 1.26+

## 동작 방식

프록시는 HTTP 메서드와 요청 URL을 조합해 캐시 키를 생성합니다.

```text
GET:/products
```

요청 처리 흐름은 다음과 같습니다.

1. 클라이언트가 프록시 서버에 요청을 보냅니다.
2. 프록시는 해당 요청 키가 메모리에 존재하는지 확인합니다.
3. 캐시에 있으면 저장된 응답을 `X-Cache: HIT`과 함께 반환합니다.
4. 캐시에 없으면 원본 서버로 요청을 전달합니다.
5. 응답을 메모리에 저장한 뒤 `X-Cache: MISS`와 함께 반환합니다.

## 프록시 실행

프록시를 실행하려면 `--port`와 `--origin`을 함께 지정해야 합니다.

```bash
go run ./cmd/cache-proxy --port <PORT> --origin <IP>
```

실행 시 예시 로그:

```text
Starting proxy server on port <PORT>
Forwarding requests to <IP>
```

실행 후 프록시로 요청을 보내면 됩니다.

```bash
curl -i http://localhost:8080/products
```

첫 번째 요청의 응답 헤더에는 다음과 같이 표시됩니다.

```text
X-Cache: MISS
```

같은 요청을 다시 보내면 다음과 같이 표시됩니다.

```text
X-Cache: HIT
```

## 캐시 비우기

메모리 캐시를 초기화하려면 아래 명령어를 사용합니다.

```bash
go run ./cmd/cache-proxy --clear-cache
```

이 명령은 현재 프록시 인스턴스가 사용 중인 메모리 캐시를 초기화합니다.

## 명령행 옵션

| 옵션 | 설명 |
| --- | --- |
| `--port` | 프록시 서버가 실행될 포트 |
| `--origin` | 요청을 전달할 원본 서버의 기본 URL |
| `--clear-cache` | 메모리 캐시 초기화 |

## 참고 사항

- 캐시는 메모리에만 저장되므로 프로세스가 종료되면 사라집니다.
- 현재 구현은 `mutex`로 보호되는 단순한 in-memory map을 사용합니다.
- 캐시 키는 요청 메서드와 URL을 기준으로 생성됩니다.
- 현재 요청 전달 로직은 원본 서버로 `GET` 요청을 보냅니다.
- TTL, 만료 정책, eviction 정책은 아직 구현되어 있지 않습니다.

## 사용 예시

프록시 실행:

```bash
go run ./cmd/cache-proxy --port 3000 --origin https://dummyjson.com
```

프록시를 통해 요청:

```bash
curl -i http://localhost:3000/products/1
curl -i http://localhost:3000/products/1
```

예상 동작:

- 첫 번째 호출: `X-Cache: MISS`
- 두 번째 호출: `X-Cache: HIT`

## 향후 개선 아이디어

- TTL 기반 캐시 만료 기능 추가
- 더 많은 HTTP 메서드와 요청 바디 지원
- 요청 헤더를 원본 서버로 함께 전달
- 캐시 적중/미적중 동작에 대한 테스트 추가
- 환경변수를 통한 설정 지원

## 라이선스

외부에 공개하거나 배포할 계획이 있다면 여기에 라이선스를 추가하면 됩니다.
