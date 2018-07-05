package com.cadiducho.bot.scheduler;

import com.cadiducho.bot.BotServer;
import lombok.Getter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class BotTask extends FutureTask<Void> {

    /**
     * The next task ID pending.
     */
    private static final AtomicInteger nextTaskId = new AtomicInteger(0);

    /**
     * The ID of this task.
     */
    @Getter private final int taskId;

    /**
     * The current number of ticks since last initialization.
     */
    private long counter;

    /**
     * The number of ticks before the call to the Runnable.
     */
    @Getter private final long delay;

    /**
     * The number of ticks between each call to the Runnable.
     */
    @Getter private final long period;

    /**
     * A flag indicating whether this task is to be run asynchronously
     */
    @Getter private final boolean sync;

    /**
     * The thread this task has been last executed on, if this task is async.
     */
    private Thread executionThread;

    /**
     * Return the last state returned by {@link #shouldExecute()}
     */
    private volatile TaskExecutionState lastExecutionState = TaskExecutionState.WAIT;

    public static BotTask sync(Runnable task) {
        return syncLater(task, 0);
    }

    public static BotTask syncLater(Runnable task, long delay) {
        return new BotTask(task, true, delay, -1);
    }

    public static BotTask async(Runnable task) {
        return new BotTask(task, false, 0, -1);
    }

    private BotTask(Runnable task, boolean sync, long delay, long period) {
        super(task, null);
        taskId = nextTaskId.getAndIncrement();
        this.delay = delay;
        this.period = period;
        counter = 0;
        this.sync = sync;
    }

    /**
     * Called every 'pulse' which is around 50ms in Minecraft. This method
     * updates the counters and returns whether execute() should be called
     *
     * @return Execution state for this task
     */
    public TaskExecutionState shouldExecute() {
        TaskExecutionState execState = shouldExecuteUpdate();
        lastExecutionState = execState;
        return execState;
    }

    private TaskExecutionState shouldExecuteUpdate() {
        if (isDone()) // Stop running if cancelled, exception, or not repeating
            return TaskExecutionState.STOP;

        ++counter;
        if (counter >= delay) {
            if (period == -1 || (counter - delay) % period == 0) {
                return TaskExecutionState.RUN;
            }
        }

        return TaskExecutionState.WAIT;
    }

    /**
     * Return the last execution state returned by {@link #shouldExecute()}
     *
     * @return the last state (most likely the state the task is currently in)
     */
    public TaskExecutionState getLastExecutionState() {
        return lastExecutionState;
    }

    @Override
    public void run() {
        executionThread = Thread.currentThread();
        if (period == -1) {
            super.run();
        } else {
            runAndReset();
        }
    }

    @Override
    protected void done() {
        super.done();
        if (isCancelled()) {
            return;
        }

        try {
            get();
        } catch (ExecutionException ex) {
            BotServer.logger.log(Level.SEVERE, "Error while executing {0}: {1}", new Object[]{this, ex.toString()});
        } catch (InterruptedException e) {
            // Task is already done, see the fact that we're in done() method
        }
    }
}
