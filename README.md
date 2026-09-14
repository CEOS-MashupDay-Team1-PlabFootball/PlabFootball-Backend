# PlabFootball Backend

플랩풋볼 서비스 분석 및 개선을 위한 CEOS Mashup Day 백엔드 프로젝트입니다.

## Requirements

- Java 21
- Gradle Wrapper

## Run

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
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
