FROM openjdk:17-oracle

COPY build/libs/backend-0.0.1-SNAPSHOT.jar backend.jar
COPY dev.entrypoint dev.entrypoint
RUN chmod 755 /dev.entrypoint

EXPOSE 8080
ENTRYPOINT ["dev.entrypoint"]