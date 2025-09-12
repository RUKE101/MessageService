//package ru.afonskiy.messenger.aspect;
//
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import ru.afonskiy.messenger.service.misc.SendLogsService;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class LoggingAspect {
//    private final SendLogsService sendLogsService;
//
//    @AfterThrowing(
//            pointcut = "execution(* ru.afonskiy.messenger.service..*(..))",
//            throwing = "throwable"
//    )
//    public void logException(JoinPoint joinPoint, Throwable throwable) {
//        Object[] args = joinPoint.getArgs();
//        String token = null;
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null && authentication.getCredentials() instanceof String) {
//            token = (String) authentication.getCredentials();
//        }
//
//        if(token == null) {
//            throw new IllegalStateException("Bearer token is null");
//        }
//        sendLogsService.sendLogs(
//                "Exception in " + joinPoint.getSignature(),
//                throwable.getMessage(),
//                token
//        );
//    }
//}
