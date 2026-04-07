# GitHub User Activity CLI

GitHub 사용자의 최근 공개 활동을 조회하여 터미널에 출력하는 간단한 Java CLI 애플리케이션입니다.

이 프로젝트는 roadmap.sh의 [GitHub User Activity](https://roadmap.sh/projects/github-user-activity) 주제를 바탕으로 만들었습니다.

## 주요 기능

- GitHub 사용자의 최근 공개 활동 조회
- 터미널에서 읽기 쉬운 형태로 활동 내역 출력
- 다음과 같은 주요 이벤트 타입 처리
  - `PushEvent`
  - `IssuesEvent`
  - `WatchEvent`
  - `ForkEvent`
  - `CreateEvent`
- 존재하지 않는 사용자명 입력 시 안내 메시지 출력

## 기술 스택

- Java 17
- Gradle
- Gson
- GitHub REST API

## 실행 환경

프로젝트를 실행하기 전에 아래 항목이 필요합니다.

- Java 17 이상


## 실행 방법

프로젝트 루트 디렉터리에서 아래 명령어를 실행하세요.

```powershell
.\gradlew.bat run --args="GitHub사용자명"
```


## 실행 예시

```text
Pushed 2 commit(s) to octocat/Hello-World
Opened an issue in octocat/Hello-World
Starred sindresorhus/awesome
Created branch in octocat/Hello-World
```


## 참고 사항

- 이 애플리케이션은 GitHub 공개 이벤트 API를 사용합니다.
  - `https://api.github.com/users/{username}/events`
- 공개된 활동만 조회할 수 있습니다.
- 출력 결과는 GitHub API가 반환하는 이벤트 종류에 따라 달라질 수 있습니다.

