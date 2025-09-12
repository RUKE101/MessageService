const WebSocket = require('ws'); // npm install ws
const Stomp = require('@stomp/stompjs'); // npm install @stomp/stompjs

// Токен авторизации
const kc_jwt = 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4NUMzRmFXLWtnSFF1TTZLREIxcVN4SVlSaU9ROFVHSU1mVXQtNmRZTWFJIn0.eyJleHAiOjE3NTc2NTU1NDAsImlhdCI6MTc1NzYxOTU0MCwianRpIjoib25ydHJvOjg3MzIzYmQyLTAwNmQtN2EwMy1hNjc2LWExMWQ1NWU5MTY4MyIsImlzcyI6Imh0dHBzOi8vdm1hZm9uc2tpeS5ydS9hdXRoL3JlYWxtcy9BdXRoT25LZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJhMmJjOWRjMy02ZTcwLTQ2MWYtODNhMy0yYzExOGVhZTAxNjUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJBdXRoT25LZXljbG9hayIsInNpZCI6IjE1MTYzN2VjLTRkYzgtNGExNy05ZWIzLTJlYmM2MTY5ZjA3NSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly92bWFmb25za2l5LnJ1LyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1hdXRob25rZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJBdXRoT25LZXljbG9hayI6eyJyb2xlcyI6WyJNT0RFUiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoidGVzdG1haW4xMjMgdGVzdG1haW4xMjMiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0bWFpbjExMSIsImdpdmVuX25hbWUiOiJ0ZXN0bWFpbjEyMyIsImZhbWlseV9uYW1lIjoidGVzdG1haW4xMjMiLCJlbWFpbCI6InZsYWRvc0B2bGFkb3MxLnJ1In0.K0-xh9icQ4wFKCm25KNz1Mk6qd8D27lMc-abbkOGepT4Ke0xknLw1PiFudDD6Tt7DwkyFcrSA3GymG6lC_myAo8knKVLhdjQwFN9RAJECo05ulAyXVcseUhYkz79A-iDsmIF8i1svpNP9k6g0EXfZW43fwtWpjtYienXwCg38ihWDPkdCJQerW3tnhD2AanhoZwoV5x3-z8tz5okhnd_oeL16_f6_kClh_aGTqymFzISR91NzU-SQT0bRgGWoRxCAToe5Erl54MwS51Xgd9HIVXG4Fkeis1oXATH1HFNwD4qlJ_A_3qWoZX9L5VXswFPNAVrTAWIQrQeDrd3QDKJlA'; // заменить на реальный токен

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
