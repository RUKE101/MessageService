const WebSocket = require('ws'); // npm install ws
const Stomp = require('@stomp/stompjs'); // npm install @stomp/stompjs

// Токен авторизации
const kc_jwt = 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4NUMzRmFXLWtnSFF1TTZLREIxcVN4SVlSaU9ROFVHSU1mVXQtNmRZTWFJIn0.eyJleHAiOjE3NTc3MjY1MTMsImlhdCI6MTc1NzY5MDUxMywianRpIjoib25ydHJvOmZkNTQ5ZGM0LTk0MDgtOWM1MC01MmY4LTY5YTU3MWU2NTJlMCIsImlzcyI6Imh0dHBzOi8vdm1hZm9uc2tpeS5ydS9hdXRoL3JlYWxtcy9BdXRoT25LZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2MTJlNGVlMC04YjNmLTRjODQtYWQ2Yy0wNDVjZWFiZTdhYjgiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJBdXRoT25LZXljbG9hayIsInNpZCI6IjJjNDY2ODgzLTg5ODAtNGZlYS1hYmNmLWQyNjRkZGY2YmQxMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly92bWFmb25za2l5LnJ1LyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1hdXRob25rZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJNT0RFUiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3RtYWluMTIzIHRlc3RtYWluMTIzIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGVzdG1haW4xIiwiZ2l2ZW5fbmFtZSI6InRlc3RtYWluMTIzIiwiZmFtaWx5X25hbWUiOiJ0ZXN0bWFpbjEyMyIsImVtYWlsIjoiYWZvbnNraXkudmxhZDAyQG1haWwucnUifQ.OapK8eqSke4SOO9qiPRN1LUFTfEA9iyM0VpyG9x0oBG-yt1yPc7w-iRwyOXfygCzN4FCNVAre_E_rcP99mV8RPGeO7UDwWgJtSTEPt3a3lA8XGRqmIkg0PEYnoPbfRLokMXEXYMjr7Qar1ogMa93yXtK7ihBGtpKAAmPIuicovNzTb4GZEYpwMWc9lIkjDP-B1Xn9qByYh9kbhOiWmA5ZpNDjATf3a5KHXalc7ux9RByo-7eSiaT8giqdGZp7mGkWSwQdoFq0D-Tt4wSPKQRgZ9ROt1geRriCSOA41Zk9k6FE39y-76-aW56tCYDCG7j0H12A9FzX_6LPp-LBXktrA'

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
  const subscription = stompClient.subscribe('/user/queue/ack', (message) => {
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
