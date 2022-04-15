package com.jinju.FirmwareServiceTransfer.interceptors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckInterceptor implements HandlerInterceptor {

    @Value("${firmwareVersion}")
    String firmware_version;

    @Value("${countConcurrent}")
    int count_concurrent;

    @Value("${maxConcurrent}")
    int maxConcurrent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (count_concurrent >= maxConcurrent) return false;
        System.out.println("interceptor..." + ++count_concurrent);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        count_concurrent--;
        System.out.println("out----" + count_concurrent);
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }


}
