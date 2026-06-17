# E-IMZO Guice

Короткая инструкция для запуска проекта на Windows Local PC.

## Требования

- Установлен и запущен Docker Desktop.
- Порты `80`, `8080`, `8081` свободны.
- На компьютере есть папка `C:\e-imzo-server` с файлами распакованные с zip файла (файл находится на телеграм канале E-IMZO Knowledge Base https://t.me/c/1649793225/59).
- Внутри `C:\e-imzo-server` должны быть `keys` и `test-config.properties`. Обращайтесь к Замире опа @baxaabdu

## Что входит в проект

Проект запускается через Docker Compose и использует:

- `nginx`;
- `e-imzo-server`;
- Java web app на Guice и Jetty.

## Запуск

Откройте терминал в папке текущего проекта `e-imzo-guice` и выполните:

```powershell
docker compose up --build
```

## Проверка

API:

```text
http://localhost:8080/ping
```

Должен вернуть JSON.

Веб-сайт:

```text
http://localhost/demo
```

## Остановка

```powershell
docker compose down
```
