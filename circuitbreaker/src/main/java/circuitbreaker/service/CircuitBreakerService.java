package circuitbreaker.service;

import circuitbreaker.breaker.CircuitBreaker;

public abstract class CircuitBreakerService<Request,Response> {

    private CircuitBreaker circuitBreaker;

    public CircuitBreakerService(CircuitBreaker circuitBreaker){
        this.circuitBreaker = circuitBreaker;
    }

    public Response execute(Request request) throws Exception {

        circuitBreaker.before();

        if (circuitBreaker.isOpen()) {
            return fallBack(request);
        }

        Response response;

        try {

            response = invoke(request);

        } catch (Exception e) {

            circuitBreaker.afterException();

            throw e;
        }

        return response;
    }

    /**
     * 业务层覆盖发方法，为熔断时执行的业务方法
     */
    abstract protected Response invoke(Request request) throws Exception;

    /**
     * 业务层覆盖该方法，熔断后会执行该方法
     * @param request
     * @return
     */
    abstract protected Response fallBack(Request request);
}
