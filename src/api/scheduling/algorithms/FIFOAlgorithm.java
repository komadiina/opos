package api.scheduling.algorithms;

import api.scheduling.tasks.*;
import java.util.*;

public class FIFOAlgorithm implements IAlgorithm {
    private Queue<JobContext> jobQueue = new ArrayDeque<>();

    @Override
    public void add(HashSet<JobContext> runningJobs, JobContext job, Integer priority) {
        jobQueue.add(job);
    }

    @Override
    public JobContext remove() { return jobQueue.poll(); }

    @Override
    public Integer count() { return jobQueue.size(); }

    @Override
    public void run(HashSet<JobContext> runningJobs, JobContext job) {
        runningJobs.add(job);
        job.start();
    }
}
