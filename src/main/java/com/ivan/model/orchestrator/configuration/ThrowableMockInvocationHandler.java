package com.ivan.model.orchestrator.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ThrowableMockInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new RuntimeException("mock exception");
    }
}
