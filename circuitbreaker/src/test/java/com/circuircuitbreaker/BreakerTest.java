package com.circuircuitbreaker;

import circuitbreaker.breaker.CircuitBreaker;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.Iterator;

@Test
public class BreakerTest {
    private CircuitBreaker breaker = new CircuitBreaker(50, 10, 200);

    private ServiceTest serviceTest = new ServiceTest(breaker);

    @DataProvider(name = "dataTest")
    public Iterator<Object[]> providerData() {

        return new DataProviderTest(2,5000);
    }

    @Test(dataProvider = "dataTest", threadPoolSize = 3, invocationCount = 5, timeOut = 1000)
    public void testBreaker(int param) {
        try {
            final Boolean result = serviceTest.execute(param);
        } catch (Exception e) {

        }

    }
}
