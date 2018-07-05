package com.cadiducho.bot.scheduler;

/**
 * Execution states for tasks
 */
public enum TaskExecutionState {

    /**
     * This task should be run this tick
     */
    RUN,

    /**
     * This task will run later, keep checking
     */
    WAIT,

    /**
     * This task will never run again, stop trying
     */
    STOP,
}