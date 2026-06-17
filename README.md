# E-IMZO Guice Demo

Servlet demo for E-IMZO using Google Guice.

## Windows PC Run With Docker

The target Windows PC does not need Java, Maven, or a locally installed nginx.
It only needs Docker Desktop and an existing `C:\e-imzo-server` folder that contains:

- `e-imzo-server.jar`
- `DockerFile`
- any files that the E-IMZO Dockerfile needs, for example `lib`, `keys`, `test-config.properties`, and `logging.properties`

This project runs three containers:

- `nginx` on public port `80`
- `e-imzo-server` on internal port `8080`
- this webapp on Jetty internal port `8081`

`nginx` uses [nginx.conf](nginx.conf):

- `/frontend` -> `http://127.0.0.1:8080`
- every other URL -> `http://127.0.0.1:8081`

The containers share one Docker network namespace, so `127.0.0.1` inside nginx can reach both the E-IMZO server and the Jetty webapp.

From this project folder, run:

```powershell
docker compose up --build
```

Then open:

```text
http://localhost/demo/
```

To stop everything:

```powershell
docker compose down
```

If port `80` is already busy, change the left side of the port mapping in [docker-compose.yml](docker-compose.yml):

```yaml
ports:
  - "8088:80"
```

Then open:

```text
http://localhost:8088/demo/
```

## Webapp Image

The webapp Docker image builds the WAR with Maven inside Docker and runs it on Jetty `11` with Java `17`.
No Java or Maven installation is required on the Windows host.

## Local Development Without Docker

For development on a machine that has JDK 17+, the webapp can still be run directly through Jetty:

```powershell
.\mvnw.cmd jetty:run
```

Open:

```text
http://localhost:8081/demo/
```

Default settings are in [src/main/resources/application.yaml](src/main/resources/application.yaml).
