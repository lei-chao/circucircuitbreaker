package com.circuircuitbreaker;

import circuitbreaker.breaker.CircuitBreaker;
import circuitbreaker.service.CircuitBreakerService;

public class ServiceTest extends CircuitBreakerService<Integer,Boolean> {

    public ServiceTest(CircuitBreaker circuitBreaker) {
        super(circuitBreaker);
    }

    @Override
    protected Boolean invoke(Integer request) throws Exception {
        System.out.println("Breaker状态：连通, request:" + request);
        if (request == 0){
            throw  new Exception("异常");
        }
        return false;
    }

    @Override
    protected Boolean fallBack(Integer request) {

        System.out.println("Breaker状态：断开, request:" + request);
        return true;
    }
}
