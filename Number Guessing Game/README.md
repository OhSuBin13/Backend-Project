# Number Guessing Game

Java로 만든 콘솔 기반 숫자 맞히기 게임입니다.

프로그램은 1부터 100 사이의 임의의 숫자를 생성하고, 플레이어는 선택한 난이도에 따라 제한된 횟수 안에 정답을 맞혀야 합니다.

## 프로젝트 소개

이 프로젝트는 Java 기본 문법과 콘솔 입출력을 활용해 만든 간단한 미니 게임입니다.  
사용자는 난이도를 선택한 뒤 숫자를 입력하며, 정답보다 큰지 작은지에 대한 힌트를 받아 정답을 추리할 수 있습니다.

## 주요 기능

- 1부터 100 사이의 랜덤 숫자 생성
- 난이도 선택 기능
- 난이도별 시도 횟수 차등 적용
  - Easy: 10번
  - Medium: 5번
  - Hard: 3번
- 오답 입력 시 Up/Down 형태의 힌트 제공
- 정답을 맞히면 시도 횟수와 함께 성공 메시지 출력

## 프로젝트 구조

```text
Number Guessing Game/
|-- src/
|   `-- NumberGuessingGame.java
|-- out/
`-- README.md
```

## 실행 환경

- Java 17 이상 권장

## 실행 방법

### 1. 컴파일

```bash
javac -d out src/NumberGuessingGame.java
```

### 2. 실행

```bash
java -cp out NumberGuessingGame
```

## 게임 진행 방식

1. 프로그램을 실행합니다.
2. 난이도를 선택합니다.
3. 숫자를 입력해 정답을 추측합니다.
4. 정답보다 큰 수를 입력했는지, 작은 수를 입력했는지 힌트를 받습니다.
5. 정답을 맞히거나 기회를 모두 소진할 때까지 반복합니다.

## 예시 출력

```text
Welcome to the Number Guessing Game!
I'm thinking of a number between 1 and 100.
You have 5 chances to guess the correct number.

Please select the difficulty level:
1. Easy (10 chances)
2. Medium (5 chances)
3. Hard (3 chances)

Enter your choice: 2

Great! You have selected the Medium difficulty level.
You have 5 chances.
Let's start the game!

Enter your choice: 50
Incorrect! The number is greater than 50
```

## 구현 내용

- `Math.random()`을 사용해 정답 숫자를 생성합니다.
- 난이도 선택값에 따라 시도 가능 횟수를 설정합니다.
- 사용자의 입력값과 정답을 비교해 결과 메시지를 출력합니다.
- 정답을 맞힌 경우 총 시도 횟수를 함께 안내합니다.

## 현재 한계

- 잘못된 난이도 번호를 입력하면 예외가 발생할 수 있습니다.
- 숫자가 아닌 값을 입력하면 예외가 발생할 수 있습니다.
- 모든 기회를 소진했을 때 별도의 게임 오버 메시지가 출력되지 않습니다.
- `Scanner`가 여러 번 생성되는 구조라 개선 여지가 있습니다.

## 개선 아이디어

- 입력값 예외 처리 추가
- 게임 오버 메시지와 정답 공개 기능 추가
- 재시작 기능 추가
- 난이도 커스터마이징 기능 추가
- 코드 구조 개선 및 테스트 코드 작성

