#  docker image build --target release --tag docker4skeleton .

FROM maven:3.8.2-jdk-11 AS builder

ARG SONAR_HOST_URL
ARG SONAR_PROJECT_KEY
ARG SONAR_TOKEN

COPY pom.xml /
COPY .m2/settings.xml /.m2/settings.xml

FROM builder AS ci

ARG SONAR_HOST_URL
ARG SONAR_PROJECT_KEY
ARG SONAR_TOKEN

  RUN ls -l /.m2
  RUN echo "-->" ${SONAR_HOST_URL} ${SONAR_PROJECT_KEY} "<--"
  RUN mvn -s .m2/settings.xml --batch-mode test
  RUN mvn -s .m2/settings.xml --batch-mode -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_TOKEN}  verify sonar:sonar



