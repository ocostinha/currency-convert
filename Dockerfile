FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

ENV DB_URL=jdbc:h2:file:./db/codingTest \
    DB_DRIVER_CLASS_NAME=org.h2.Driver \
    DB_USERNAME=felipepaltrinieri \
    DB_PASSWORD=wex \
    SERVER_PORT=8080 \
    MANAGEMENT_PORT=9090 \
    FISCAL_DATA_BASE_URL=https://api.fiscaldata.treasury.gov/services/api/fiscal_service \
    FISCAL_DATA_RATES_PATH=/v1/accounting/od/rates_of_exchange \
    FISCAL_DATA_MAX_MONTH_AGO=6 \
    FISCAL_DATA_CONNECT_TIMEOUT_MS=5000 \
    FISCAL_DATA_READ_TIMEOUT_MS=10000 \
    FISCAL_DATA_MAX_RETRIES=3 \
    RATE_LIMIT_CAPACITY=50 \
    RATE_LIMIT_REFILL_TOKENS=50 \
    RATE_LIMIT_REFILL_DURATION=1 \
    SHUTDOWN_TIMEOUT=30s

EXPOSE 8080 9090

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9090/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-Xlog:gc*:file=/app/logs/gc.log:time,uptime:filecount=5,filesize=10M", \
    "-jar", "app.jar"]
