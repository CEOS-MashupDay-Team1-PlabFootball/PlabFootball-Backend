# PlabFootball Backend

플랩풋볼 서비스 분석 및 개선을 위한 CEOS Mashup Day 백엔드 프로젝트입니다.

## Requirements

- Java 21
- Gradle Wrapper
- MySQL

## 개발 환경

| 항목 | 구성 |
| --- | --- |
| Spring Boot | 4.1.1 |
| Gradle | Wrapper 9.7.1 |
| HTTP / 입력 검증 | Spring Web MVC / Validation |
| 데이터 저장 | Spring Data JPA / MySQL |
| API 문서 | Springdoc OpenAPI 3.1.1 / Swagger UI |
| 반복 코드 생성 | Lombok |

## DB 준비

MySQL에서 개발용 데이터베이스를 준비합니다.

```sql
CREATE DATABASE IF NOT EXISTS plabfootball CHARACTER SET utf8mb4;
```

실행할 터미널 또는 IntelliJ의 실행 설정에 다음 환경변수를 지정합니다.

| 환경변수 | 의미 | 예시 |
| --- | --- | --- |
| `DB_URL` | MySQL JDBC 접속 주소 | `jdbc:mysql://localhost:3306/plabfootball` |
| `DB_USERNAME` | DB 사용자 | `root` 또는 개발용 계정 |
| `DB_PASSWORD` | DB 비밀번호 | 해당 계정의 비밀번호; 없으면 빈 문자열 |

실제 계정 정보는 소스 파일에 저장하지 않습니다.

```bash
export DB_URL='jdbc:mysql://localhost:3306/plabfootball'
export DB_USERNAME='root'
export DB_PASSWORD='본인의 MySQL 비밀번호'
```

## Run

```bash
./gradlew bootRun
```

Java 버전이 여러 개 설치된 macOS에서는 아래와 같이 Java 21을 선택할 수 있습니다.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

`application.yml`은 DB 연결값을 환경변수에서 읽고 `open-in-view=false`를 적용합니다.
`ddl-auto=update`로 개발용 테이블을 생성·변경합니다.
현재 설정은 `application.yml` 하나로 관리하며 별도의 프로필 지정 없이 실행합니다.

## Test

```bash
./gradlew test
```

테스트 코드는 기존 Health Check 확인만 유지합니다.
DB 연결, 공통 응답·예외 처리와 API 문서 동작은 개발 환경에서 별도로 확인합니다.

전체 빌드:

```bash
./gradlew clean build
```

## Health Check

서버가 실행된 뒤 아래 요청으로 애플리케이션 실행 상태를 확인할 수 있습니다.

```bash
curl http://localhost:8080/api/health
```

응답:

```json
{
  "status": "ok"
}
```

## API 문서

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 패키지 구성

- `health`: 서버 실행 상태 확인
- `global/config`: JPA Auditing 및 Swagger 설정
- `global/apiPayload`: 공통 응답
- `global/apiPayload/code`: 성공·오류 코드 인터페이스 및 Reason DTO
- `global/apiPayload/code/status`: 공통 성공·오류 코드 enum
- `global/apiPayload/exception`: 업무 예외
- `global/apiPayload/exception/handler`: 전역 예외 처리
- `global/entity`: 생성·수정 시각을 가진 공통 Entity

이후 기능 코드는 `domain/기능명` 아래의 `controller`, `service`, `repository`, `entity`,
`dto`로 나눕니다. 공통 초기 설정에는 도메인별 Entity나 인증·배포 구현을 포함하지 않습니다.

## 공통 응답 및 예외

성공 응답은 `ApiResponse.onSuccess(result)`를 사용합니다.

```json
{
  "isSuccess": true,
  "code": "200",
  "message": "성공입니다.",
  "result": {
    "name": "example"
  }
}
```

`result`가 null이면 해당 필드는 생략됩니다. `201 Created` 등 다른 성공 상태는
`SuccessStatus`로 응답을 만들고 `ResponseEntity`의 HTTP 상태도 함께 지정합니다.
Health Check는 간단한 `{"status":"ok"}` 응답을 사용합니다.

성공 코드는 `BaseCode`, 오류 코드는 `BaseErrorCode`를 구현합니다.
`getReason()`은 코드·메시지를 담은 DTO를, `getReasonHttpStatus()`는 HTTP 상태까지 포함한 DTO를 반환합니다.
성공은 `ReasonDTO`, 오류는 `ErrorReasonDTO`로 정보를 전달합니다.
`ApiResponse.of(BaseCode, result)`와 `GeneralException(BaseErrorCode)`는 공통 enum과 도메인별 enum을 함께 지원합니다.
`ApiResponse`와 Reason DTO는 일반 클래스이며 Builder로 생성합니다.

업무상 오류는 `new GeneralException(ErrorStatus._NOT_FOUND)`처럼 발생시킵니다.
전역 핸들러는 오류에 맞는 HTTP 상태와 공통 응답을 반환합니다.
입력 검증 실패는 400과 필드별 오류를 반환하며, 잘못된 JSON·파라미터와 404·405·415 등
Spring MVC 오류도 원래 HTTP 상태를 유지합니다. 예상치 못한 서버 오류의 상세 내용은
서버 로그에 기록하고 응답에는 노출하지 않습니다.

```json
{
  "isSuccess": false,
  "code": "COMMON404",
  "message": "요청한 대상을 찾을 수 없습니다."
}
```
