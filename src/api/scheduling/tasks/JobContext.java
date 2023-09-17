package api.scheduling.tasks;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class JobContext implements IJobContext, Comparable<JobContext> {
    private enum State {
        NOT_STARTED,
        RUNNING,
        RUNNING_WITH_PAUSE_REQUEST,
        WAITING_RESUMAL,
        PAUSED,
        FINISHED
    }

    private State jobState = State.NOT_STARTED;
    private final Thread thread;
    private final Object contextLock = new Object();
    private final Consumer<JobContext> onFinished, onPaused, onContinueRequested;
    private final Semaphore finishedSemaphore = new Semaphore(0);
    private final Semaphore resumeSemaphore = new Semaphore(0);
    public Integer priority = null;
    private Integer numWaiters = 0;

    public JobContext(IUserJob job, Integer priority,
                      Consumer<JobContext> onFinished,
                      Consumer<JobContext> onPaused,
                      Consumer<JobContext> onContinueRequested) {
        this.thread = new Thread(() -> {
           try {
               job.run(this);
           }
           finally {
               this.finish();
           }
        });

        this.priority = priority;
        this.onFinished = onFinished;
        this.onPaused = onPaused;
        this.onContinueRequested = onContinueRequested;
    }

    public void start() {
        synchronized (contextLock) {
            switch (jobState) {
                case NOT_STARTED -> {
                    jobState = State.RUNNING;
                    thread.start();
                }
                case RUNNING_WITH_PAUSE_REQUEST, RUNNING -> {
                    throw new IllegalStateException("Can't start, job already started.");
                }
                case FINISHED -> {
                    throw new IllegalStateException("Can't start, job already finished.");
                }
                case WAITING_RESUMAL -> {
                    jobState = State.RUNNING;
                    resumeSemaphore.release();
                }
                default -> throw new IllegalStateException("Unexpected value: " + jobState);
            }
        }
    }

    private void finish() {
        synchronized (contextLock) {
            switch (jobState) {
                case NOT_STARTED -> throw new IllegalStateException("Can't finish, job not started.");
                case RUNNING_WITH_PAUSE_REQUEST, RUNNING -> {
                    jobState = State.FINISHED;
                    if (numWaiters > 0)
                        finishedSemaphore.release(numWaiters);

                    onFinished.accept(this);
                }
                case FINISHED -> throw new IllegalStateException("Can't finish, job already finished.");
                default -> throw new IllegalStateException("Unexpected value: " + jobState);
            }
        }
    }

    public void waitContext() {
        synchronized (contextLock) {
            switch (jobState) {
                case NOT_STARTED, RUNNING_WITH_PAUSE_REQUEST, RUNNING -> {
                    numWaiters++;
                }
                case FINISHED -> {
                    return;
                }
                default -> throw new IllegalStateException("Unexpected value: " + jobState);
            }
        }

        try {
            finishedSemaphore.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void requestPause() {
        synchronized (contextLock) {
            switch (jobState) {
                case NOT_STARTED, RUNNING_WITH_PAUSE_REQUEST, PAUSED, FINISHED -> {}
                case RUNNING -> jobState = State.RUNNING_WITH_PAUSE_REQUEST;
                default -> throw new IllegalStateException("Unexpected value: " + jobState);
            }
        }
    }

    public void requestContinue() {
        synchronized (contextLock) {
            switch (jobState) {
                case NOT_STARTED, RUNNING, FINISHED -> {}
                case RUNNING_WITH_PAUSE_REQUEST -> jobState = State.RUNNING;
                case PAUSED -> {
                    jobState = State.WAITING_RESUMAL;
                    onContinueRequested.accept(this);
                }
                default -> throw new IllegalStateException("Unexpected value: " + jobState);
            }
        }
    }

    @Override
    public void checkPause() {
        boolean shouldPause = false;
        synchronized (contextLock) {
            switch (jobState) {
                case NOT_STARTED -> throw new IllegalStateException("Can't pause, job not started.");
                case RUNNING -> {}
                case RUNNING_WITH_PAUSE_REQUEST -> {
                    jobState = State.PAUSED;
                    onPaused.accept(this);
                    shouldPause = true;
                }
                case FINISHED -> throw new IllegalStateException("Can't pause, job already finished.");
                default -> throw new IllegalStateException("Unexpected value: " + jobState);
            }
        }

        if (shouldPause) {
            try {
                resumeSemaphore.acquire();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int compareTo(JobContext other) {
        return Integer.compare(this.priority, other.priority);
    }
}
