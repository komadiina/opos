package api.scheduling;

import api.scheduling.algorithms.*;
import api.scheduling.tasks.*;
import java.util.*;

public class Scheduler {
    public enum Algorithm {
        FIFO, PRIORITY
    }

    public Integer maxConcurrentTasks = 1;
    private IAlgorithm jobQueue = new FIFOAlgorithm();
    private HashSet<JobContext> runningJobs = new HashSet<JobContext>();
    private final Object schedulerLock = new Object();

    public Job schedule(JobSpecification jobSpecification) {
        JobContext jobContext = new JobContext(jobSpecification.job, jobSpecification.priority,
                this::jobFinishedHandler, this::jobPausedHandler, this::jobContinueRequestedHandler);

        synchronized (schedulerLock) {
            if (runningJobs.size() < maxConcurrentTasks) {
                runningJobs.add(jobContext);
                jobContext.start();
            } else {
                jobQueue.add(runningJobs, jobContext, jobContext.priority);
            }
        }

        return new Job(jobContext);
    }

    private void jobFinishedHandler(JobContext context) {
        synchronized (schedulerLock) {
            runningJobs.remove(context);

            if (jobQueue.size() > 0) {
                JobContext jobContext = jobQueue.poll();
                runningJobs.add(jobContext);
                jobContext.start();
            }
        }
    }

    private void jobPausedHandler(JobContext context) {
        synchronized (schedulerLock) {
            runningJobs.remove(context);

            if (jobQueue.size() > 0) {
                JobContext jobContext = jobQueue.poll();
                runningJobs.add(jobContext);
                jobContext.start();
            }
        }
    }

    private void jobContinueRequestedHandler(JobContext context) {
        synchronized (schedulerLock) {
            if (runningJobs.size() < maxConcurrentTasks) {
                runningJobs.add(context);
                context.start();
            } else {
                jobQueue.add(runningJobs, context, context.priority);
            }
        }
    }

    public void setAlgorithm(IAlgorithm algorithm) {
        this.jobQueue = algorithm;
    }
}
