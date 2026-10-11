FROM eclipse-temurin:21-jre-noble@sha256:000fd431958bc81a24abe1e8e5f0f0fd3ae365a594bd50aadb20696805f9408c

WORKDIR /app

RUN groupadd --gid 10001 app \
    && useradd --uid 10001 --gid app --no-create-home --shell /usr/sbin/nologin app

RUN curl -fsSL https://truststore.pki.rds.amazonaws.com/ap-northeast-2/ap-northeast-2-bundle.pem -o /tmp/rds-bundle.pem \
    && awk '/-----BEGIN CERTIFICATE-----/ { n++; file=sprintf("/tmp/rds-ca-%d.pem", n) } file { print > file } /-----END CERTIFICATE-----/ { close(file); file="" }' /tmp/rds-bundle.pem \
    && for cert in /tmp/rds-ca-*.pem; do \
        keytool -importcert -noprompt -cacerts -storepass changeit \
            -alias "$(basename "$cert" .pem)" -file "$cert" || exit 1; \
    done \
    && rm /tmp/rds-bundle.pem /tmp/rds-ca-*.pem

ARG JAR_FILE=build/libs/*.jar
COPY --chown=app:app ${JAR_FILE} app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
