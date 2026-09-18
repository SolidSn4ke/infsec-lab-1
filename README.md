# Информационная безопасность. Лабораторная работа 1

Разработка защищенного REST API с интеграцией в CI/CD

- [Информационная безопасность. Лабораторная работа 1](#информационная-безопасность-лабораторная-работа-1)
  - [Описание проекта](#описание-проекта)
    - [Описание API](#описание-api)
      - [GET /api/data](#get-apidata)
        - [Query](#query)
        - [Response](#response)
      - [POST /api/posts](#post-apiposts)
        - [Body](#body)
        - [Response](#response-1)
      - [POST /auth/login](#post-authlogin)
        - [Body](#body-1)
        - [Response](#response-2)
    - [Подробное описание реализованных мер защиты](#подробное-описание-реализованных-мер-защиты)
      - [SQL Injection](#sql-injection)
      - [XSS](#xss)
      - [Реализация аутентификации](#реализация-аутентификации)
    - [Скриншоты отчетов SAST/SCA](#скриншоты-отчетов-sastsca)
      - [SAST](#sast)
      - [SCA](#sca)

## Описание проекта

Spring Boot веб-сервис, предоставляющий доступ к созданию и просмотру постов для зарегистрированных пользователей

### Описание API

#### GET /api/data

Получить список постов с пагинацией и фильтрацией

##### Query

| Param | Type   | Desc                             |
| ----- | ------ | -------------------------------- |
| page  | int    | Номер страницы. 0 по умолчанию   |
| size  | int    | Размер страницы. 20 по умолчанию |
| sort  | String | Сортировка. Пример: `uuid,desc`  |

##### Response

- 200 OK

```json
{
    "items": [
        {
            "body": "My first post",
            "createdAt": "2026-09-18T11:53:36.779029",
            "postedBy": "qwerty",
            "title": "Hello",
            "uuid": "a1167300-b7f7-408d-b240-043b5db69b52"
        }
    ],
    "page": 0,
    "size": 5,
    "totalElements": 1,
    "totalPages": 1
}
```

- 403 Forbidden

Неверный или отсутствующий jwt токен

```json
{
    "timestamp": "2026-09-18T13:27:01.531Z",
    "status": 403,
    "error": "Forbidden",
    "message": "Forbidden",
    "path": "/api/data"
}
```

#### POST /api/posts

Создать пост

##### Body

| Param | Type   | Desc            |
| ----- | ------ | --------------- |
| title | String | Заголовок поста |
| body  | String | Текст поста     |

##### Response

- 200 OK

```json
{
    "body": "My first post",
    "createdAt": "2026-09-18T11:53:36.779029",
    "postedBy": "qwerty",
    "title": "Hello",
    "uuid": "a1167300-b7f7-408d-b240-043b5db69b52"
}
```

- 400 Bad Request

Неверное тело запроса

```json
{
    "timestamp": "2026-09-18T13:39:23.288Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid title",
    "path": "/api/posts"
}
```

- 403 Forbidden

Неверный или отсутствующий jwt токен

```json
{
    "timestamp": "2026-09-18T13:27:48.593Z",
    "status": 403,
    "error": "Forbidden",
    "message": "Forbidden",
    "path": "/api/posts"
}
```

#### POST /auth/login

Эндпоинт для получения jwt токена. Регистрирует пользователя, если его еще нет в базе данных. Если пользователь уже зарегистрирован, то сверяет введенный пароль с тем, что есть в бд

##### Body

| Param    | Type   | Desc               |
| -------- | ------ | ------------------ |
| login    | String | Логин пользователя |
| password | String | Пароль             |

##### Response

- 200 OK

```json
{
    "expirationDate": "2026-10-18T13:41:41.519Z",
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzg5NzM4OTAxLCJleHAiOjE3OTIzMzA5MDF9.0TzunP4Gg2nV5GDikg5W12chQ5evbL-wz8U5HNSa4D4",
    "tokenType": "Bearer"
}
```

- 400 Bad Request

Неверный формат логина или пароля

```json
{
    "timestamp": "2026-09-18T13:41:25.596Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid password",
    "path": "/auth/login"
}
```

- 403 Forbidden

Пароли не совпадают

```json
{
    "timestamp": "2026-09-18T13:40:19.255Z",
    "status": 403,
    "error": "Forbidden",
    "message": "Wrong password",
    "path": "/auth/login"
}
```

### Подробное описание реализованных мер защиты

#### SQL Injection

В качестве ORM провайдера был выбран Hibernate. Для всех обращений к базе данных были использованы методы, встроенные в Spring Data. Они по умолчанию имеют защиту от sql injection, так как внутри используется `PreparedStatements`

#### XSS

Меры предпринятые для защиты от XSS

- Санитизация данных

Все данные, которые возвращает веб сервис, санитизируются за счет внутренних функций фреймворка

```java
    public void setTitle(String title) {
        this.title = title == null ? null : HtmlUtils.htmlEscape(title);
    }

    public void setBody(String body) {
        this.body = body == null ? null : HtmlUtils.htmlEscape(body);
    }
```

- Возвращение данных в формате `application/json`
- Запрет на использование специальных символов

```java
    public void validateUserLoginRequest(UserLoginRequest request) {
        if (request.getLogin() == null || !request.getLogin().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid login");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6
                || request.getPassword().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }
    }
```

#### Реализация аутентификации

Процесс аутентификации

1. Отправка запроса POST /auth/login
2. Проверка валидности запроса. Если запрос невалиден - вернуть 400 Bad Request
3. Проверка присутствует ли указанный логин в БД
   1. Если нет, то происходит регистрация и возвращается jwt токен
   2. Если да, то происходит сравнение указанного пароля с тем, что присутствует в БД. Если хеши совпадают, то возвращается jwt токен, иначе 403 Forbidden

На защищенных эндпоинтах GET /api/data и POST /api/posts используется `JwtAuthFilter` для проверки подлинности jwt токена

Для хеширования паролей используется алгоритм bcrypt

### Скриншоты отчетов SAST/SCA

#### SAST

В качестве SAST инструмента был взят SpotBugs

![Spotbugs report](./res/Screenshot%202026-09-18%20at%2017.42.12.png)

#### SCA

В качестве SCA инструмента был взят OWASP Dependency-Check

![OWASP Dependency-Check](./res/Screenshot%202026-09-18%20at%2017.43.34.png)