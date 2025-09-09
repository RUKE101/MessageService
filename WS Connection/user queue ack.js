const WebSocket = require('ws'); // npm install ws
const Stomp = require('@stomp/stompjs'); // npm install @stomp/stompjs

// Токен авторизации
const kc_jwt = 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4NUMzRmFXLWtnSFF1TTZLREIxcVN4SVlSaU9ROFVHSU1mVXQtNmRZTWFJIn0.eyJleHAiOjE3NTc1NTExOTMsImlhdCI6MTc1NzUxNTE5MywianRpIjoib25ydHJvOmQzMWQ0ZTRmLTRlN2UtYWJmZC05MDYzLWJiODkzOGFlOTc3YiIsImlzcyI6Imh0dHBzOi8vdm1hZm9uc2tpeS5ydS9hdXRoL3JlYWxtcy9BdXRoT25LZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2MTJlNGVlMC04YjNmLTRjODQtYWQ2Yy0wNDVjZWFiZTdhYjgiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJBdXRoT25LZXljbG9hayIsInNpZCI6ImNmZDRhMzdkLWJlMjUtNGVjMy1hNDcwLTBhMWU1NGUwNDcxMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly92bWFmb25za2l5LnJ1LyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1hdXRob25rZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJNT0RFUiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3RtYWluMTIzIHRlc3RtYWluMTIzIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGVzdG1haW4xIiwiZ2l2ZW5fbmFtZSI6InRlc3RtYWluMTIzIiwiZmFtaWx5X25hbWUiOiJ0ZXN0bWFpbjEyMyIsImVtYWlsIjoiYWZvbnNraXkudmxhZDAyQG1haWwucnUifQ.NXpOmzCNcn6h1uV4NpOt2ZDTa6GR--vI7hCmG3QhbIZRiiOf61OJV5S2Wb-ohA09Cb_i70XYMsFFTKoMGl1RIyH1TFp6EHHfqXihO6soUP8CsNmKK6uJXdWaT3ZAKsiqwKANTGr_Vn2F-7zV_lbPtF-ZxVnNcT8nlG-d06HAuXEzaK_cl2FTe_auciAXbVfAIby-bx0GUTFJRT4EuxAPOI2YoI-517BRAE7XQPnPV8J-Gyx-iIV7A60ZgYPIZGL_KJOsHhAifMJLw8teXsbVijAj647dG_1bHUHf2X_7KZtUj0dI_Pp7RnpfbMoN0xbCWR0-dISjh62m65c0cOapIQ'; // заменить на реальный токен

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
