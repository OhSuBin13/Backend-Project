# Broadcast Server

간단한 비동기 WebSocket 브로드캐스트 예제 프로젝트입니다.

이 프로젝트는 서버 콘솔에서 입력한 메시지를 현재 연결된 모든 클라이언트에게 전송합니다. Python의 `asyncio`, `websockets`, `aioconsole`을 사용해 가볍게 실시간 통신 구조를 실습할 수 있도록 구성되어 있습니다.

## 프로젝트 구성

- `server.py`: WebSocket 서버를 실행하고, 연결된 클라이언트 목록을 관리하며, 콘솔에서 입력한 메시지를 전체 클라이언트에게 브로드캐스트합니다.
- `client.py`: 서버에 접속한 뒤, 서버가 보낸 메시지를 계속 수신하여 콘솔에 출력합니다.

## 동작 방식

1. 서버가 `localhost:8765`에서 실행됩니다.
2. 클라이언트가 서버에 접속하면 서버는 연결된 클라이언트 수를 출력합니다.
3. 서버 콘솔에 메시지를 입력하면 현재 연결된 모든 클라이언트에게 같은 메시지가 전송됩니다.
4. 클라이언트는 수신한 메시지를 콘솔에 출력합니다.


## 실행 환경

- Python 3.13 이상 권장


## 설치 방법

가상환경을 사용하는 경우:

```powershell
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

가상환경을 사용하지 않는 경우:

```powershell
pip install -r requirements.txt
```

## 실행 방법

### 1. 서버 실행

```powershell
python server.py
```

서버가 실행되면 `localhost:8765`에서 연결을 기다립니다. 콘솔에는 현재 접속 중인 클라이언트 수와 입력 프롬프트가 표시됩니다.

### 2. 클라이언트 실행

새 터미널을 열고 아래 명령으로 클라이언트를 실행합니다.

```powershell
python client.py
```


## 실행 예시

서버 터미널:

```text
Total Clients: 2
> hello
```

클라이언트 터미널:

```text
Waiting for messages...
[Server]: hello
```

## 예외 및 참고 사항

- 서버는 현재 `localhost`에만 바인딩되어 있으므로, 같은 PC 내에서만 접속할 수 있습니다.
- 클라이언트를 창 닫기 등으로 즉시 종료하면 WebSocket close frame 없이 연결이 끊어질 수 있어 종료 관련 예외가 발생할 수 있습니다.
- 현재 서버는 클라이언트가 보낸 메시지를 별도로 처리하지 않습니다.
- 연결 중인 클라이언트가 없을 때 서버에서 메시지를 입력하면 전송 대상이 없으므로 화면상 변화가 거의 없을 수 있습니다.


