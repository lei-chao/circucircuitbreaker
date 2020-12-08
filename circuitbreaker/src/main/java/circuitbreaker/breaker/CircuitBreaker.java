package circuitbreaker.breaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断器实现类
 */
public class CircuitBreaker {
    /**
     * 错误次数
     */
    private AtomicInteger failureCount = new AtomicInteger();

    /**
     * 样本次数
     */
    private int sampleSize;

    /**
     * 错误次数阀值
     */
    private int failureThreshold;

    /**
     * 恢复时间间隔
     */
    private long timeout;

    /**
     * 恢复时间点
     */
    private volatile long recoverTime;

    /**
     * 已调用次数
     */
    private AtomicInteger hasAccessSampleSize = new AtomicInteger();

    /**
     * 当前熔断器状态 true:开启 false:关闭
     */
    private Boolean isOpen;


    public boolean isOpen() {
        //是否到达失败阀值
        if (failureCount.intValue() == failureThreshold){
            resetRecoverTime();
            isOpen = true;
            return true;
        }

        //超过阀值并且没到间隔时间
        if (failureCount.intValue() > failureThreshold && !isTimeToClose()){
            return true;
        }

        //到达间隔时间
        if (isOpen && isTimeToClose()){
            isOpen = false;
            resetFailureCount();
        }
        return isOpen;
    }

    /**
     * 调用次数增加
     */

    protected void increaseAccesssCount() {
        hasAccessSampleSize.incrementAndGet();
    }

    /**
     * 失败次数递增
     */
    protected void increaseFailureCount() {
        failureCount.incrementAndGet();
    }

    /**
     * 重置失败次数
     */
    public void resetFailureCount() {
        failureCount = new AtomicInteger(0);
    }

    /**
     * 重置恢复时间
     */
    public void resetRecoverTime(){
        recoverTime = System.currentTimeMillis() + timeout;
    }

    /**
     * 到达关闭时间
     */
    public boolean isTimeToClose(){
        return System.currentTimeMillis() >= recoverTime;
    }

    /**
     * 校验样本次数
     */
    private void checkAccessTime(){
        if (hasAccessSampleSize.intValue() <= sampleSize){
            return;
        }
        hasAccessSampleSize = new AtomicInteger(0);
    }

    /**
     * 初始化熔断器，熔断器切换到闭合状态
     */
    public CircuitBreaker(int failureThreshold, int sampleSize, int timeout)
    {
        if (failureThreshold < 1 || sampleSize < 1) {
            throw new RuntimeException("熔断器闭合状态的最大失败次数和采样次数必须大于0");
        }
        if (timeout < 1) {
            throw new RuntimeException("熔断器断开状态超时时间必须大于0");
        }
        this.failureThreshold = failureThreshold;
        this.sampleSize = sampleSize;
        this.timeout = timeout;
        isOpen = false;
    }

    public void before() {
        increaseAccesssCount();
        //如果是断开状态，直接返回
        if (isOpen()) {
            throw new RuntimeException("服务已熔断，请稍等重试！");
        }
        checkAccessTime();
    }

    /**
     * 方法调用发生异常操作后的操作
     */
    public void afterException() {
        //增加失败次数
        increaseFailureCount();
    }
}
