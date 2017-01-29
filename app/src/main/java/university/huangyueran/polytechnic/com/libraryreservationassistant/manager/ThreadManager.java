package university.huangyueran.polytechnic.com.libraryreservationassistant.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程管理器
 * Created by huangyueran on 2017/1/22.
 */
public class ThreadManager {

    private static ThreadPool mThreadPool; //单利线程池

    public static ThreadPool getThreadPool() {
        if (mThreadPool == null) {
            synchronized (ThreadManager.class) {
                if (mThreadPool == null) {
                    int cpuCount = Runtime.getRuntime().availableProcessors();// cpu个数
                    System.out.println("cpu个数:" + cpuCount);
                    //int threadCount = cpuCount * 2 + 1; //线程池个数
                    int threadCount = 10;
                    mThreadPool = new ThreadPool(threadCount, threadCount, 0L);
                }
            }
        }
        return mThreadPool;
    }

    /**
     * 单例线程池
     */
    public static class ThreadPool {

        private int corePoolSize; //核心线程数
        private int maximumPoolSize; // 最大线程数
        private long keepAliveTime; // 线程保持活跃时间
        private ThreadPoolExecutor poolExecutor;

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        // 线程池几个参数的理解:
        // 比如去火车站买票, 有10个售票窗口, 但只有5个窗口对外开放. 那么对外开放的5个窗口称为核心线程数,
        // 而最大线程数是10个窗口.
        // 如果5个窗口都被占用, 那么后来的人就必须在后面排队, 但后来售票厅人越来越多, 已经人满为患, 就类似于线程队列已满.
        // 这时候火车站站长下令, 把剩下的5个窗口也打开, 也就是目前已经有10个窗口同时运行. 后来又来了一批人,
        // 10个窗口也处理不过来了, 而且售票厅人已经满了, 这时候站长就下令封锁入口,不允许其他人再进来, 这就是线程异常处理策略.
        // 而线程存活时间指的是, 允许售票员休息的最长时间, 以此限制售票员偷懒的行为.
        public void execute(Runnable r) {
            if (poolExecutor == null) {
                // 参1:核心线程数;参2:最大线程数;参3:线程休眠时间;参4:时间单位;参5:线程队列;参6:生产线程的工厂;参7:线程异常处理策略
                poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                        TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(),
                        Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }

            //执行一个Runnable对象,由线程池管理
            poolExecutor.execute(r);
        }

        /**
         * 取消任务
         */
        public void cancel(Runnable r) {
            if (poolExecutor != null && r != null) {
                poolExecutor.remove(r); // 从线程队列中移除一个runnable对象
            }
        }

    }

}
