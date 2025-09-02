const WebSocket = require('ws'); // npm install ws
const Stomp = require('@stomp/stompjs'); // npm install @stomp/stompjs

// Токен авторизации
const kc_jwt = 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4NUMzRmFXLWtnSFF1TTZLREIxcVN4SVlSaU9ROFVHSU1mVXQtNmRZTWFJIn0.eyJleHAiOjE3NTcwMzU2OTgsImlhdCI6MTc1Njk5OTY5OCwianRpIjoib25ydHJvOmVhNWU0YzRiLTE0MTMtYjRlMy1jYzMwLTEwNTIwNGVhYjVhNCIsImlzcyI6Imh0dHBzOi8vdm1hZm9uc2tpeS5ydS9hdXRoL3JlYWxtcy9BdXRoT25LZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2MTJlNGVlMC04YjNmLTRjODQtYWQ2Yy0wNDVjZWFiZTdhYjgiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJBdXRoT25LZXljbG9hayIsInNpZCI6ImZkYjU1MWE0LWRjNzAtNDQxYy04ZjM3LTgxZTI4NWFhNThlNiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly92bWFmb25za2l5LnJ1LyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1hdXRob25rZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJNT0RFUiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3RtYWluMTIzIHRlc3RtYWluMTIzIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGVzdG1haW4xIiwiZ2l2ZW5fbmFtZSI6InRlc3RtYWluMTIzIiwiZmFtaWx5X25hbWUiOiJ0ZXN0bWFpbjEyMyIsImVtYWlsIjoiYWZvbnNraXkudmxhZDAyQG1haWwucnUifQ.Jb2v2vroKMbGOm4OlN3a5Aqk6ePXD1NLzhIGEbugxjyuWOweNVoddNsP8d-IQH8dt67UoLqiGC-t89F2YQXLPP1LqItVsklPP7hN4YguiB9B9v3TLoBf29iR32PG_6IABJYWn9DvdIZLd2nQ4d-YTCfkf02_breYIP_N50RYadF37OY6UeVWhMXD8d8Sy0c9TXtG5KWK3lsJKpbf0N-e7_8P5XshJPq_QtHoLpURg_V_KEcWFYsFc8kpz7jBzpwW9nJkfM-V6RvQhaJYHyc2zmSCrXnP8sD3jhtaAi_ls_zMRt56MIl5Oe_dOPWDz7cksOY9EtI2MmWq41TqEDzOpg'; // заменить на реальный токен

// Создаем WebSocket с нужным URL (пример)
const ws = new WebSocket('ws://localhost:8082/ws', {
  headers: {
    'Authorization': `Bearer ${kc_jwt}` // заголовок HTTP при первоначальном подключении
  }
});

// Создаем клиент STOMP поверх WebSocket
const stompClient = new Stomp.Client({
  webSocketFactory: () => ws,
                                     // Необходимо передать заголовок авторизации еще и в STOMP connect
                                     connectHeaders: {
                                       Authorization: `Bearer ${kc_jwt}`
                                     },
                                     // Автоматическое переподключение при падении
                                     reconnectDelay: 15000,
                                     debug: (str) => {
                                       console.log(str);
                                     }
});

// Подключение STOMP клиента
stompClient.onConnect = (frame) => {
  console.log('Connected: ' + frame);

  // Подписываемся на destination с id sub-0
  const subscription = stompClient.subscribe('/user/queue/messages', (message) => {
    console.log('Received message: ' + message.body);
    // здесь можно обрабатывать полученные сообщения
  }, { id: 'sub-0' });

  // Если нужно, можно потом отписаться
  // subscription.unsubscribe();
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

// Запускаем подключение
stompClient.activate();
