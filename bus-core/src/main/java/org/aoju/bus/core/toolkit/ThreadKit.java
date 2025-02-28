/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.core.toolkit;

import org.aoju.bus.core.thread.ExecutorBuilder;
import org.aoju.bus.core.thread.GlobalThread;
import org.aoju.bus.core.thread.NamedThreadFactory;
import org.aoju.bus.core.thread.ThreadBuilder;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.*;

/**
 * 线程池工具
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class ThreadKit {

    /**
     * 新建一个线程池
     * <pre>
     *    1. 初始线程数为corePoolSize指定的大小
     *    2. 没有最大线程数限制
     *    3. 默认使用LinkedBlockingQueue，默认队列大小为1024
     *    4. 当运行线程大于corePoolSize放入队列，队列满后抛出异常
     * </pre>
     *
     * @param corePoolSize 同时执行的线程数大小
     * @return ExecutorService
     */
    public static ExecutorService newExecutor(int corePoolSize) {
        return ExecutorBuilder.create().setCorePoolSize(corePoolSize).build();
    }

    /**
     * 获得一个新的线程池
     * <pre>
     *    1. 初始线程数为 0
     *    2. 最大线程数为Integer.MAX_VALUE
     *    3. 使用SynchronousQueue
     *    4. 任务直接提交给线程而不保持它们
     * </pre>
     *
     * @return ExecutorService
     */
    public static ExecutorService newExecutor() {
        return ExecutorBuilder.create().setWorkQueue(new SynchronousQueue<>()).build();
    }

    /**
     * 获得一个新的线程池
     * 如果maximumPoolSize &gt;= corePoolSize，在没有新任务加入的情况下，多出的线程将最多保留60s
     *
     * @param corePoolSize    初始线程池大小
     * @param maximumPoolSize 最大线程池大小
     * @return {@link ThreadPoolExecutor}
     */
    public static ThreadPoolExecutor newExecutor(int corePoolSize, int maximumPoolSize) {
        return ExecutorBuilder.create()
                .setCorePoolSize(corePoolSize)
                .setMaxPoolSize(maximumPoolSize)
                .build();
    }

    /**
     * 获得一个新的线程池，并指定最大任务队列大小
     * 如果maximumPoolSize &gt;= corePoolSize，在没有新任务加入的情况下，多出的线程将最多保留60s
     *
     * @param corePoolSize     初始线程池大小
     * @param maximumPoolSize  最大线程池大小
     * @param maximumQueueSize 最大任务队列大小
     * @return {@link ThreadPoolExecutor}
     */
    public static ExecutorService newExecutor(int corePoolSize, int maximumPoolSize, int maximumQueueSize) {
        return ExecutorBuilder.create()
                .setCorePoolSize(corePoolSize)
                .setMaxPoolSize(maximumPoolSize)
                .setWorkQueue(new LinkedBlockingQueue<>(maximumQueueSize))
                .build();
    }

    /**
     * 获得一个新的线程池,只有单个线程
     * <pre>
     *    1. 初始线程数为 1
     *    2. 最大线程数为 1
     *    3. 默认使用LinkedBlockingQueue，默认队列大小为1024
     *    4. 同时只允许一个线程工作，剩余放入队列等待，等待数超过1024报错
     * </pre>
     *
     * @return ExecutorService
     */
    public static ExecutorService newSingleExecutor() {
        return ExecutorBuilder.create()
                .setCorePoolSize(1)
                .setMaxPoolSize(1)
                .setKeepAliveTime(0)
                .buildFinalizable();
    }

    /**
     * 获得一个新的线程池
     * 传入阻塞系数,线程池的大小计算公式为：CPU可用核心数 / (1 - 阻塞因子)
     * Blocking Coefficient(阻塞系数) = 阻塞时间／(阻塞时间+使用CPU的时间)
     * 计算密集型任务的阻塞系数为0,而IO密集型任务的阻塞系数则接近于1
     * <p>
     * see: http://blog.csdn.net/partner4java/article/details/9417663
     *
     * @param blockingCoefficient 阻塞系数,阻塞因子介于0~1之间的数,阻塞因子越大,线程池中的线程数越多
     * @return {@link ThreadPoolExecutor}
     */
    public static ThreadPoolExecutor newExecutorByBlockingCoefficient(float blockingCoefficient) {
        if (blockingCoefficient >= 1 || blockingCoefficient < 0) {
            throw new IllegalArgumentException("[blockingCoefficient] must between 0 and 1, or equals 0.");
        }

        // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
        int poolSize = (int) (Runtime.getRuntime().availableProcessors() / (1 - blockingCoefficient));
        return ExecutorBuilder.create().setCorePoolSize(poolSize).setMaxPoolSize(poolSize).setKeepAliveTime(0L).build();
    }

    /**
     * 直接在公共线程池中执行线程
     *
     * @param runnable 可运行对象
     */
    public static void execute(Runnable runnable) {
        GlobalThread.execute(runnable);
    }

    /**
     * 执行异步方法
     *
     * @param runnable 需要执行的方法体
     * @param isDeamon 是否守护线程 守护线程会在主线程结束后自动结束
     * @return 执行的方法体
     */
    public static Runnable excAsync(final Runnable runnable, boolean isDeamon) {
        Thread thread = new Thread(() -> runnable.run());
        thread.setDaemon(isDeamon);
        thread.start();

        return runnable;
    }

    /**
     * 执行有返回值的异步方法
     * Future代表一个异步执行的操作,通过get()方法可以获得操作的结果,如果异步操作还没有完成,则,get()会使当前线程阻塞
     *
     * @param <T>  回调对象类型
     * @param task {@link Callable}
     * @return Future
     */
    public static <T> Future<T> execAsync(Callable<T> task) {
        return GlobalThread.submit(task);
    }

    /**
     * 执行有返回值的异步方法
     * Future代表一个异步执行的操作,通过get()方法可以获得操作的结果,如果异步操作还没有完成,则,get()会使当前线程阻塞
     *
     * @param runnable 可运行对象
     * @return {@link Future}
     */
    public static Future<?> execAsync(Runnable runnable) {
        return GlobalThread.submit(runnable);
    }

    /**
     * 新建一个CompletionService,调用其submit方法可以异步执行多个任务,最后调用take方法按照完成的顺序获得其结果
     * 若未完成,则会阻塞
     *
     * @param <T> 回调对象类型
     * @return CompletionService
     */
    public static <T> CompletionService<T> newCompletionService() {
        return new ExecutorCompletionService<>(GlobalThread.getExecutor());
    }

    /**
     * 新建一个CompletionService,调用其submit方法可以异步执行多个任务,最后调用take方法按照完成的顺序获得其结果
     * 若未完成,则会阻塞
     *
     * @param <T>      回调对象类型
     * @param executor 执行器 {@link ExecutorService}
     * @return CompletionService
     */
    public static <T> CompletionService<T> newCompletionService(ExecutorService executor) {
        return new ExecutorCompletionService<>(executor);
    }

    /**
     * 新建一个CountDownLatch,一个同步辅助类,在完成一组正在其他线程中执行的操作之前,它允许一个或多个线程一直等待
     *
     * @param threadCount 线程数量
     * @return CountDownLatch
     */
    public static CountDownLatch newCountDownLatch(int threadCount) {
        return new CountDownLatch(threadCount);
    }

    /**
     * 创建新线程,非守护线程,正常优先级,线程组与当前线程的线程组一致
     *
     * @param runnable {@link Runnable}
     * @param name     线程名
     * @return {@link Thread}
     */
    public static Thread newThread(Runnable runnable, String name) {
        final Thread t = newThread(runnable, name, false);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    /**
     * 创建新线程
     *
     * @param runnable {@link Runnable}
     * @param name     线程名
     * @param isDeamon 是否守护线程
     * @return {@link Thread}
     */
    public static Thread newThread(Runnable runnable, String name, boolean isDeamon) {
        final Thread t = new Thread(null, runnable, name);
        t.setDaemon(isDeamon);
        return t;
    }

    /**
     * 挂起当前线程
     *
     * @param timeout  挂起的时长
     * @param timeUnit 时长单位
     * @return 被中断返回false, 否则true
     */
    public static boolean sleep(Number timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout.longValue());
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * 挂起当前线程
     *
     * @param millis 挂起的毫秒数
     * @return 被中断返回false，否则true
     */
    public static boolean sleep(Number millis) {
        if (millis == null) {
            return true;
        }
        return sleep(millis.longValue());
    }

    /**
     * 挂起当前线程
     *
     * @param millis 挂起的毫秒数
     * @return 被中断返回false，否则true
     */
    public static boolean sleep(long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 考虑{@link Thread#sleep(long)}方法有可能时间不足给定毫秒数,此方法保证sleep时间不小于给定的毫秒数
     *
     * @param millis 给定的sleep时间
     * @return 被中断返回false, 否则true
     * @see ThreadKit#sleep(Number)
     */
    public static boolean safeSleep(Number millis) {
        if (millis == null) {
            return true;
        }

        return safeSleep(millis.longValue());
    }

    /**
     * 考虑{@link Thread#sleep(long)}方法有可能时间不足给定毫秒数，此方法保证sleep时间不小于给定的毫秒数
     *
     * @param millis 给定的sleep时间
     * @return 被中断返回false，否则true
     * @see ThreadKit#sleep(Number)
     */
    public static boolean safeSleep(long millis) {
        long done = 0;
        long before;
        long spendTime;
        while (done >= 0 && done < millis) {
            before = System.currentTimeMillis();
            if (false == sleep(millis - done)) {
                return false;
            }
            spendTime = System.currentTimeMillis() - before;
            if (spendTime <= 0) {
                // Sleep花费时间为0或者负数，说明系统时间被拨动
                break;
            }
            done += spendTime;
        }
        return true;
    }

    /**
     * @return 获得堆栈列表
     */
    public static StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }

    /**
     * 获得堆栈项
     *
     * @param i 第几个堆栈项
     * @return 堆栈项
     */
    public static StackTraceElement getStackTraceElement(int i) {
        StackTraceElement[] stackTrace = getStackTrace();
        if (i < 0) {
            i += stackTrace.length;
        }
        return stackTrace[i];
    }

    /**
     * 创建本地线程对象
     *
     * @param <T>           持有对象类型
     * @param isInheritable 是否为子线程提供从父线程那里继承的值
     * @return 本地线程
     */
    public static <T> ThreadLocal<T> createThreadLocal(boolean isInheritable) {
        if (isInheritable) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    }

    /**
     * 创建ThreadFactoryBuilder
     *
     * @return ThreadFactoryBuilder
     * @see ThreadBuilder#build()
     */
    public static ThreadBuilder createThreadFactoryBuilder() {
        return ThreadBuilder.create();
    }

    /**
     * 结束线程,调用此方法后,线程将抛出 {@link InterruptedException}异常
     *
     * @param thread 线程
     * @param isJoin 是否等待结束
     */
    public static void interrupt(Thread thread, boolean isJoin) {
        if (null != thread && false == thread.isInterrupted()) {
            thread.interrupt();
            if (isJoin) {
                waitFor(thread);
            }
        }
    }

    /**
     * 等待当前线程结束. 调用 {@link Thread#join()} 并忽略 {@link InterruptedException}
     */
    public static void waitFor() {
        waitFor(Thread.currentThread());
    }

    /**
     * 等待线程结束. 调用 {@link Thread#join()} 并忽略 {@link InterruptedException}
     *
     * @param thread 线程
     */
    public static void waitFor(Thread thread) {
        boolean dead = false;
        do {
            try {
                thread.join();
                dead = true;
            } catch (InterruptedException e) {
                // ignore
            }
        } while (!dead);
    }

    /**
     * 获取JVM中与当前线程同组的所有线程
     *
     * @return 线程对象数组
     */
    public static Thread[] getThreads() {
        return getThreads(Thread.currentThread().getThreadGroup().getParent());
    }

    /**
     * 获取JVM中与当前线程同组的所有线程
     * 使用数组二次拷贝方式,防止在线程列表获取过程中线程终止
     * from Voovan
     *
     * @param group 线程组
     * @return 线程对象数组
     */
    public static Thread[] getThreads(ThreadGroup group) {
        final Thread[] slackList = new Thread[group.activeCount() * 2];
        final int actualSize = group.enumerate(slackList);
        final Thread[] result = new Thread[actualSize];
        System.arraycopy(slackList, 0, result, 0, actualSize);
        return result;
    }

    /**
     * 获取进程的主线程
     * from Voovan
     *
     * @return 进程的主线程
     */
    public static Thread getMainThread() {
        for (Thread thread : getThreads()) {
            if (thread.getId() == 1) {
                return thread;
            }
        }
        return null;
    }

    /**
     * 获取当前线程的线程组
     *
     * @return 线程组
     */
    public static ThreadGroup currentThreadGroup() {
        final SecurityManager s = System.getSecurityManager();
        return (null != s) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    /**
     * 创建线程工厂
     *
     * @param prefix   线程名前缀
     * @param isDeamon 是否守护线程
     * @return the object
     */
    public static ThreadFactory newNamedThreadFactory(String prefix, boolean isDeamon) {
        return new NamedThreadFactory(prefix, isDeamon);
    }

    /**
     * 创建线程工厂
     *
     * @param prefix      线程名前缀
     * @param threadGroup 线程组,可以为null
     * @param isDeamon    是否守护线程
     * @return the object
     */
    public static ThreadFactory newNamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDeamon) {
        return new NamedThreadFactory(prefix, threadGroup, isDeamon);
    }

    /**
     * 创建线程工厂
     *
     * @param prefix      线程名前缀
     * @param threadGroup 线程组,可以为null
     * @param isDeamon    是否守护线程
     * @param handler     未捕获异常处理
     * @return the object
     */
    public static ThreadFactory newNamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDeamon, UncaughtExceptionHandler handler) {
        return new NamedThreadFactory(prefix, threadGroup, isDeamon, handler);
    }

    /**
     * 阻塞当前线程,保证在main方法中执行不被退出
     *
     * @param obj 对象所在线程
     */
    public static void sync(Object obj) {
        synchronized (obj) {
            try {
                obj.wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}
