package com.cadiducho.bot.scheduler;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BotScheduler {

    /**
     * The scheduled executor service
     */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(BotThreadFactory.INSTANCE);

    /**
     * Executor to handle execution of async tasks
     */
    private final ExecutorService asyncTaskExecutor = Executors.newCachedThreadPool(BotThreadFactory.INSTANCE);

    /**
     * The primary thread in which pulse() is called
     */
    private Thread primaryThread;

    /**
     * A list of active tasks.
     */
    private final ConcurrentMap<Integer, BotTask> tasks = new ConcurrentHashMap<>();

    public void start() {
        executor.scheduleAtFixedRate(this::pulse, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void pulse() {
        primaryThread = Thread.currentThread();

        // Run the relevant tasks.
        for (Iterator<BotTask> it = tasks.values().iterator(); it.hasNext(); ) {
            BotTask task = it.next();
            switch (task.shouldExecute()) {
                case RUN:
                    if (task.isSync()) {
                        task.run();
                    } else {
                        asyncTaskExecutor.submit(task);
                    }
                    break;
                case STOP:
                    it.remove();
            }
        }
    }

    public void stop() {
        executor.shutdownNow();
    }

    public boolean isPrimaryThread() {
        return Thread.currentThread() == primaryThread;
    }

    public BotTask schedule(BotTask task) {
        tasks.put(task.getTaskId(), task);
        return task;
    }

    private static class BotThreadFactory implements ThreadFactory {
        static final BotThreadFactory INSTANCE = new BotThreadFactory();
        private final AtomicInteger threadCounter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Bot-scheduler-" + threadCounter.getAndIncrement());
        }
    }
}
