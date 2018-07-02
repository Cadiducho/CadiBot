package com.cadiducho.bot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BotScheduler {

    /**
     * The scheduled executor service
     */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(BotThreadFactory.INSTANCE);

    /**
     * The primary thread in which pulse() is called
     */
    private Thread primaryThread;

    public void start() {
        executor.scheduleAtFixedRate(this::pulse, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void pulse() {
        primaryThread = Thread.currentThread();
    }

    public void stop() {
        executor.shutdownNow();
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
