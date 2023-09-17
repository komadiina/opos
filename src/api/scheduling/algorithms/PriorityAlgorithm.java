package api.scheduling.algorithms;

import java.util.*;
import api.scheduling.tasks.*;

public class PriorityAlgorithm implements IAlgorithm {
    private PriorityQueue<JobContext> jobQueue = new PriorityQueue<>();

    @Override
    public void add(HashSet<JobContext> runningJobs, JobContext job, Integer priority) {
        job.priority = priority;
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
