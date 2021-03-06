package circuitbreaker.breaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断器实现类
 */
public class CircuitBreaker {
    /**
     * 错误次数
     */
    private Integer failureCount = 0;

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
    private volatile long recoverTime = Long.MAX_VALUE;

    /**
     * 已调用次数
     */
    private AtomicInteger hasAccessSampleSize = new AtomicInteger();

    /**
     * 当前熔断器状态 true:开启 false:关闭
     */
    private boolean isOpen;

    public boolean getOpen() {
        return isOpen;
    }

    /**
     * 调用次数增加
     */

    public void increaseAccessCount() {
        hasAccessSampleSize.incrementAndGet();
    }

    /**
     * 重置失败次数
     */
    public void resetFailureCount() {
        failureCount = 0;
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
        isOpen = isOpenState();
    }

    public synchronized void before() {
        increaseAccessCount();
        //如果是断开状态，直接返回
        if (isOpenState()) {
            return;
        }
        checkAccessTime();
    }

    /**
     * 方法调用发生异常操作后的操作
     */
    public synchronized void afterException() {
        //增加失败次数
        if (++failureCount == failureThreshold){
            resetRecoverTime();
        }
    }

    private synchronized boolean isOpenState() {
        //是否到达失败阀值
        if (failureCount >= failureThreshold){
            isOpen = true;
        }

        //到达间隔时间
        if (isTimeToClose()){
            System.out.println("达到熔断时间关闭熔断器");
            isOpen = false;
            //重置失败次数
            resetFailureCount();
            //重置恢复时间
            recoverTime = Long.MAX_VALUE;
        }
        return isOpen;
    }
}
