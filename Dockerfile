FROM gcr.io/distroless/java17@sha256:64967fe3051702640c68bd434813b91a3fc9182f8894962f7638f79a5986c31d

COPY target/ms-account-manager-*.jar /account-manager.jar


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/account-manager.jar"]
