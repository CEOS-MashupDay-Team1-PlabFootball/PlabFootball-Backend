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

## 배포

앱·Nginx 이미지를 Docker Hub에 올리고, SSH로 EC2의 Compose를 갱신합니다.
PR에서는 테스트·이미지 빌드만 실행하고 `main`·`develop` push 또는 두 브랜치의 수동 실행에서 배포합니다.
두 브랜치는 같은 EC2를 사용하므로 마지막으로 배포한 버전이 운영됩니다. 다른 브랜치의 수동 실행은 빌드·배포하지 않습니다.
Docker Hub 저장소는 `wannys26/plabfootball-backend`, `wannys26/plabfootball-nginx`입니다.

```bash
./gradlew clean test bootJar
docker build --platform linux/amd64 -t wannys26/plabfootball-backend:manual .
docker build --platform linux/amd64 -f Dockerfile-nginx -t wannys26/plabfootball-nginx:manual .
```

최초 수동 배포에서는 위 두 이미지를 Docker Hub에 로그인해 업로드하고 `.env`의 `manual` 태그를 사용합니다.
배포 이미지는 commit SHA 태그로 발행하고 digest로 고정합니다.

GitHub Actions Secrets에 `DOCKER_TOKEN`, `EC2_HOST`, `EC2_SSH_KEY`, `EC2_FINGERPRINT`를 등록합니다.
토큰은 Read & Write 권한을 사용합니다. SSH 호스트 지문은 이미 신뢰한 EC2 호스트 키에서 확인합니다.
워크플로의 수동 실행은 파일이 기본 브랜치에 반영된 뒤 가능합니다.

EC2의 `/home/ubuntu/app`에 `docker-compose.yml`과 `.env`를 준비합니다.
`.env.example`을 복사해 실제 이미지 주소와 배포 DB 암호를 입력하고 `chmod 600 .env`를 실행합니다.
암호에 `$`가 있으면 작은따옴표로 감싸고, 작은따옴표 자체는 `\'`로 이스케이프합니다.
로컬 개발은 로컬 DB 환경변수를, EC2는 `.env`의 RDS 연결값을 사용합니다.

자동 배포에서는 배포할 커밋의 `docker-compose.yml`을 임시 경로에 전송합니다.
서버의 기존 Compose와 `.env`를 각각 `docker-compose.yml.previous`, `.env.previous`로 백업한 뒤 새 설정과 이미지 주소를 적용합니다.
DB 비밀번호 등 서버의 `.env` 값은 유지하고 이미지 주소 두 개만 갱신합니다.

Nginx를 실행하기 전에 EC2에서 인증서를 발급합니다. 이메일 주소는 실제 값으로 바꿉니다.

```bash
sudo docker run --rm -p 80:80 -v /etc/letsencrypt:/etc/letsencrypt \
  certbot/certbot@sha256:f70ad0adbb7e117f0fe42a63c553f28ea451edabc0148757b6efcd9735acaa20 \
  certonly --standalone -d 52.79.241.143.nip.io --email 본인이메일 \
  --agree-tos --non-interactive
```

서버에서 `sudo docker compose up -d --wait`로 실행한 뒤
`https://52.79.241.143.nip.io/api/health` 응답과 실제 RDS 조회를 확인합니다.
Docker Hub 저장소가 비공개이면 EC2에서 먼저 `sudo docker login -u wannys26`이 필요합니다.
인증서 갱신은 Certbot 컨테이너를 `--webroot -w /var/www/certbot`으로 실행하고 Nginx를 reload합니다.
갱신 주기는 EC2에서 cron으로 별도 등록합니다. EC2 IP 변경 시 Nginx 도메인과 인증서도 변경합니다.

설정 검사·이미지 다운로드·기동·HTTPS 확인에 실패하면 이전 Compose와 `.env`를 함께 복구합니다. 이전 이미지를 미리 지우지 않습니다.
최초 배포에는 이전 정상 이미지가 없으며, 이미지 복구가 DB 스키마까지 되돌리지는 않습니다.

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
