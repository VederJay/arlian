FROM openjdk:16-jdk-alpine as builder
WORKDIR application

RUN apk update && apk upgrade
RUN apk add maven
RUN apk add git

RUN git clone -b develop --single-branch https://github.com/VederJay/arlian.git .
RUN mvn install -DskipTests

RUN cp target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:16-jdk-alpine
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]