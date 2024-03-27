FROM gcr.io/distroless/java17@sha256:2f01c2ff0c0db866ed73085cf1bb5437dd162b48526f89c1baa21dd77ebb5e6d
COPY target/ms-account-manager-*.jar /account-manager.jar


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/account-manager.jar"]
