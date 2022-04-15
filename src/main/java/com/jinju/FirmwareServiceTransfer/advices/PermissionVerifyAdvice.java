package com.jinju.FirmwareServiceTransfer.advices;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class PermissionVerifyAdvice {

    @Pointcut("@annotation(com.jinju.FirmwareServiceTransfer.annotations.Permission)")
    private void permissionVerifyPointcut() {
    }

    @Around("permissionVerifyPointcut()")
    public Object permissionVerify(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        HttpSession session = request.getSession();
        if (session.getAttribute("token") == null) {
            return "login";
        }

        return joinPoint.proceed();
    }

    private Map<String, String> getCookieMap(Cookie[] cookies) {
        Map<String, String> map = new HashMap<>();
        for (Cookie c : cookies) {
            map.put(c.getName(), c.getValue());
        }
        return map;
    }
}
