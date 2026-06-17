FROM eclipse-temurin:17-jdk AS build
WORKDIR /build

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src

RUN chmod +x mvnw \
    && ./mvnw -s .mvn/central-settings.xml clean package -DskipTests

FROM jetty:11-jre17

COPY --from=build /build/target/e-imzo-guice.war /var/lib/jetty/webapps/ROOT.war

USER root
RUN printf '%s\n' 'jetty.http.port=8081' > /var/lib/jetty/start.d/e-imzo-guice.ini \
    && chown jetty:jetty /var/lib/jetty/start.d/e-imzo-guice.ini \
    && chown jetty:jetty /var/lib/jetty/webapps/ROOT.war
USER jetty

EXPOSE 8081
