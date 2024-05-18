package com.ivan.model.orchestrator.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

public class ProfilingMethodInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = new Date().getTime();
        var result = method.invoke(proxy, args);
        long endTime = new Date().getTime();
        System.out.println(method.getName() + "completed for: " + (startTime - startTime));
        return result;
    }
}
