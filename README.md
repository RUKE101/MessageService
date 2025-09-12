# Messenger Microservice

Микросервис для обмена сообщениями в реальном времени, поддерживающий личные (DM) и групповые чаты. Построен на Spring Boot с использованием WebSocket (STOMP) и MongoDB в качестве базы данных.

##  Основные возможности

*   **Личные сообщения (Direct Messages):**
    *   Отправка и получение сообщений между пользователями.
    *   Получение истории переписки с конкретным пользователем.
    *   Получение непрочитанных сообщений (с автоматическим пометкой как "прочитано").
    *   Редактирование и удаление своих сообщений.

*   **Групповые чаты:**
    *   Создание групп.
    *   Отправка сообщений в группу.
    *   Получение истории сообщений группы.
    *   Редактирование и удаление своих сообщений в группе.
    *   Просмотр списка групп, в которых состоит пользователь.

*   **Безопасность:**
    *   Аутентификация и авторизация запросов с помощью JWT токена.
    *   Проверка прав пользователя на доступ к группе.

##  Технологический стек

*   **Backend:** Java, Spring Boot (Web, WebSocket)
*   **База данных:** MongoDB
*   **Аутентификация:** JWT (JSON Web Tokens)
*   **Библиотеки:** Lombok, Spring Data MongoDB



##  API Endpoints (WebSocket STOMP)

Микросервис использует STOMP поверх WebSocket для коммуникации в реальном времени.

### Конфигурация WebSocket

Клиенты должны подключиться к WebSocket endpoint (например, `ws://localhost:8080/ws`) и подписаться на соответствующие темы и очереди.

### Контроллеры и маршруты

#### 1. Личные сообщения (`DMChatWsController`)

*   **Назначение:** Отправка и управление личными сообщениями.
*   **Базовый путь:** `/app/chat`

| Маршрут (Mapping) | Назначение | Payload | Ответ (SendToUser) | Описание |
| :--- | :--- | :--- | :--- | :--- |
| `/app/chat` | Отправить сообщение | `MessageEntity` | `/queue/ack` | Отправляет сообщение указанному получателю. |
| `/app/fetchUnreadMessages` | Получить непрочитанные | Header: `authorization` | `/queue/messages` | Возвращает все непрочитанные сообщения для пользователя и помечает их как прочитанные. |
| `/app/fetchUserMessages` | История переписки | Headers: `authorization`, `sender` | `/queue/messages` | Возвращает историю сообщений с указанным пользователем. |
| `/app/deleteMessage` | Удалить сообщение | Headers: `authorization`, `id` | `/queue/ack` | Удаляет сообщение по ID. |
| `/app/changeMessage` | Изменить сообщение | Headers: `authorization`, `messageId`, `text` | `/queue/ack` | Изменяет текст сообщения по ID. |

#### 2. Групповые чаты (`GroupChatWsController`)

*   **Назначение:** Работа с групповыми чатами.
*   **Базовый путь:** `/app/group/{groupId}`

| Маршрут (Mapping) | Назначение | Payload / Headers | Ответ (SendToUser) | Описание |
| :--- | :--- | :--- | :--- | :--- |
| `/app/group/{groupId}/send` | Отправить в группу | `GroupMessageEntity` | `/queue/ack` | Отправляет сообщение в указанную группу. Широковещательно рассылает его по теме `/topic/group/{groupId}`. |
| `/app/group/{groupId}/fetch` | Получить историю группы | - | `/queue/messages` | Возвращает все сообщения из указанной группы. |
| `/app/group/{groupId}/update` | Изменить сообщение | Headers: `messageId`, `text` | `/queue/ack` | Изменяет текст сообщения в группе. |
| `/app/group/{groupId}/delete` | Удалить сообщение | Header: `messageId` | `/queue/messages` | Удаляет сообщение из группы. |

#### 3. Управление группами (`GroupControlWsController`)

*   **Назначение:** Создание и просмотр групп.
*   **Базовый путь:** `/app/group`

| Маршрут (Mapping) | Назначение | Payload | Ответ (SendToUser) | Описание |
| :--- | :--- | :--- | :--- | :--- |
| `/app/group/preferences` | Создать группу | `GroupEntity` | `/queue/ack` | Создает новую группу. Создатель автоматически добавляется в участники. |
| `/app/group/view` | Мои группы | - | `/queue/ack` | Возвращает список всех групп, в которых состоит текущий пользователь. |

### Подписки для клиентов

Клиентское приложение должно подписаться на следующие очереди для получения ответов:
*   `/user/queue/ack` - для получения подтверждений о успешном выполнении операций (отправка, удаление, изменение).
*   `/user/queue/messages` - для получения входящих личных сообщений, истории чатов и непрочитанных сообщений.
*   `/topic/group/{groupId}` - для получения в реальном времени всех новых сообщений, отправленных в группу `{groupId}`.

##  Аутентификация (JWT)

Все запросы требуют валидного JWT токена.
*   Токен должен передаваться в заголовке `authorization` при подключении к WebSocket и сохраняться в атрибутах сессии под ключом `jwt`.
*   Перед обработкой любого сообщения токен проверяется на валидность с помощью `JwtUtils`.
*   Из токена извлекается UUID и имя пользователя для идентификации отправителя и проверки прав.

##  Модели данных

### MessageEntity (Личные сообщения)
*   `id` (String) - уникальный идентификатор
*   `sender` (String) - отправитель
*   `recipient` (String) - получатель
*   `text` (String) - текст сообщения
*   `timestamp` (String) - метка времени
*   `status` (MessageStatus) - статус сообщения (UNREAD, READ)

### GroupMessageEntity (Сообщения группы)
*   `id` (String) - уникальный идентификатор
*   `groupId` (String) - идентификатор группы
*   `sender` (String) - отправитель
*   `text` (String) - текст сообщения
*   `timestamp` (String) - метка времени

### GroupEntity (Группа)
*   `id` (String) - уникальный идентификатор
*   `nameOfGroup` (String) - название группы
*   `descriptionOfGroup` (String) - описание группы
*   `ownerId` (String) - UUID владельца группы
*   `participantsIds` (List<String>) - список UUID участников группы

##  Запуск и развертывание

1.  **Убедитесь, что запущен MongoDB.**
2.  **Настройте подключение к БД** в `application.properties`:
```properties
spring.application.name=messenger
server.port=8082

spring.data.mongodb.uri=mongodb://admin:strongpassword@192.168.31.143:27017/Message?authSource=admin
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://vmafonskiy.ru/auth/realms/AuthOnKeycloak
keycloak.realm=AuthOnKeycloak
keycloak.client-id=AuthOnKeycloak
keycloak.clientSecret=9b8eoBY52MVAeYKmfY2Ds4dfpHSgqdQF
keycloak.validate-realm=AuthOnKeycloak
keycloak.url=https://vmafonskiy.ru/auth
logging.service.url=https://vmafonskiy.ru/api/v1/logging
```
3.  **Соберите и запустите приложение:**
    ```bash
    ./mvnw clean spring-boot:run
    ```

##  Пример взаимодействия (Client Side)

Пример кода на JavaScript для отправки сообщения в группу:

```javascript
// Подключение к WebSocket
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

const headers = {
    'authorization': 'Bearer YOUR_JWT_TOKEN_HERE'
};

// Подключение
stompClient.connect(headers, function(frame) {
    console.log('Connected: ' + frame);

    // Подписка на персональную очередь для подтверждений
    stompClient.subscribe('/user/queue/ack', function(message) {
        console.log('Ack: ', message.body);
    });

    // Подписка на тему группы для получения новых сообщений
    stompClient.subscribe('/topic/group/group-123', function(message) {
        console.log('New group message: ', JSON.parse(message.body));
    });
});

// Отправка сообщения в группу
function sendGroupMessage() {
    const message = {
        text: "Hello Group!",
        // другие поля...
    };
    stompClient.send("/app/group/group-123/send", {}, JSON.stringify(message));
}
```