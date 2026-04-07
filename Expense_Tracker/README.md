# Expense Tracker

간단한 Java 콘솔 기반 가계부 애플리케이션입니다. 사용자는 지출을 추가, 수정, 삭제하고 전체 지출 및 월별 합계를 확인할 수 있습니다.

## 기능

- 지출 추가
- 지출 수정
- 지출 삭제
- 전체 지출 목록 조회
- 전체 지출 합계 조회
- 특정 월 지출 합계 조회
- 종료 시 `expense.txt` 파일로 데이터 저장

## 프로젝트 구조

```text
Expense_Tracker
|-- build.gradle.kts
|-- gradlew
|-- gradlew.bat
|-- src
|   `-- main
|       `-- java
|           `-- org
|               `-- example
|                   |-- Expense.java
|                   |-- ExpenseStorage.java
|                   `-- ExpenseTracker.java
```

## 개발 환경

- Java 17 이상 권장
- Gradle Wrapper 포함

설치 여부 확인:

```powershell
java -version
```

## 빌드 방법

프로젝트 루트에서 아래 명령을 실행합니다.

```powershell
.\gradlew.bat build
```

빌드가 성공하면 컴파일된 클래스 파일은 `build\classes\java\main` 아래에 생성됩니다.

## 실행 방법

이 프로젝트는 현재 `application` 플러그인이 설정되어 있지 않으므로 `gradlew run` 대신 아래 명령으로 실행합니다.

```powershell
java -cp build\classes\java\main org.example.ExpenseTracker
```


## 사용 예시

프로그램을 실행하면 아래와 같은 메뉴가 표시됩니다.

```text
Expense Tracker Menu:
1. Add Expense
2. Update Expense
3. Delete Expense
4. View All Expense
5. View Summary of All Expenses
6. View Summary of Expenses for a specific month
7. Exit
```

번호를 입력해 원하는 기능을 선택하면 됩니다.

## 데이터 저장 방식

- 지출 데이터는 프로젝트 루트의 `expense.txt` 파일에 저장됩니다.
- 프로그램 시작 시 `expense.txt`를 읽어 기존 데이터를 불러옵니다.
- 프로그램 종료 메뉴(`7. Exit`)를 선택하면 현재 데이터가 저장됩니다.

저장 형식 예시:

```text
2026-04-01,Coffee,4500,Food
2026-04-03,Bus,1400,Transport
```

## 주의사항

- 처음 실행할 때 `expense.txt`가 없으면 파일을 찾을 수 없다는 메시지가 한 번 출력될 수 있습니다. 이후 종료 시 파일이 생성됩니다.
- `Update Expense`에서 수정할 항목은 목록의 인덱스 번호 기준으로 선택합니다.
- 날짜 형식은 `YYYY-MM-DD`, 월 조회는 `MM` 형식을 기준으로 입력합니다.
- 설명이나 카테고리에 쉼표(`,`)가 포함되면 현재 저장 형식과 충돌할 수 있습니다.

