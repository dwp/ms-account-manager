FROM gcr.io/distroless/java11@sha256:5b718692f577accb22e4efe22db20ef1effcf121d5114ec9a76f3f58ab6673c3
COPY target/ms-account-manager-*.jar /account-manager.jar

COPY --from=pik94420.live.dynatrace.com/linux/oneagent-codemodules:java / /
ENV LD_PRELOAD /opt/dynatrace/oneagent/agent/lib64/liboneagentproc.so

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/account-manager.jar"]
