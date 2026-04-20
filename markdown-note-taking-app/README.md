# 마크다운 노트 업로드 앱

간단한 Node.js 기반 웹 애플리케이션으로, 마크다운 파일을 업로드하면 서버에서 HTML로 변환해 브라우저에 바로 보여줍니다.  
또한 업로드된 파일 목록을 확인할 수 있는 별도 페이지도 제공합니다.

## 주요 기능

- `.md` 파일 업로드
- 업로드된 마크다운 내용을 HTML로 변환
- 업로드된 파일 목록 조회
- Express 기반의 가벼운 서버 구성

## 기술 스택

- Node.js
- Express
- Multer
- Marked

## 프로젝트 구조

```text
markdown-note-taking-app/
├─ index.js          # Express 서버 엔트리 포인트
├─ index.html        # 마크다운 업로드 화면
├─ list.html         # 업로드된 파일 목록 화면
├─ uploads/          # 업로드된 파일 저장 디렉터리
├─ package.json
└─ README.md
```

## 설치 방법

아래 명령어로 의존성을 설치합니다.

```bash
npm install
```

## 실행 방법

```bash
node index.js
```

## 동작 방식

1. 사용자가 메인 페이지에서 마크다운 파일을 업로드합니다.
2. 서버는 `multer`를 사용해 파일을 `uploads/` 디렉터리에 저장합니다.
3. 저장된 파일 내용을 읽어 `marked`로 HTML로 변환합니다.
4. 변환된 결과를 브라우저에 바로 응답으로 반환합니다.
5. `/files` 엔드포인트를 통해 업로드된 파일 목록을 JSON 형식으로 조회할 수 있습니다.
