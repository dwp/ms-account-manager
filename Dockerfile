FROM gcr.io/distroless/java11@sha256:520bf091f91e13f7627e60cdd1a515911ac024d178fe72266c3a8170f60082d0
COPY target/ms-account-manager-*.jar /account-manager.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/account-manager.jar"]
