# E-IMZO Guice Demo

Servlet-based E-IMZO demo migrated from the Spring Boot sample to Google Guice.

## Run with Jetty

Requires JDK 17+.

```shell
./mvnw jetty:run
```

On Windows:

```shell
.\mvnw.cmd jetty:run
```

If `.\mvnw.cmd -version` shows Java 8, point the terminal at JDK 17+ first. For the JDK installed on this machine:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd jetty:run
```

The Maven wrapper uses the project-local Central-only settings file in `.mvn/central-settings.xml`, so an unavailable user-level Maven mirror will not affect this project.

If you run Maven without the wrapper, pass the same settings file explicitly:

```shell
mvn -s .mvn\central-settings.xml jetty:run
```

Open `http://localhost:8081/demo/`.

## Run with Tomcat

Build the WAR:

```shell
.\mvnw.cmd clean package
```

Deploy `target/e-imzo-guice.war` to Tomcat 10+.

## Configuration

Defaults are in `src/main/resources/application.yaml`.

Values can be overridden with JVM system properties:

```shell
.\mvnw.cmd jetty:run -Deimzo.rest.service.host.base=http://127.0.0.1:8080 -Deimzo.rest.service.host.challenger=http://127.0.0.1:8080/frontend/challenge
```
