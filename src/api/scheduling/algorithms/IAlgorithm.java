package api.scheduling.algorithms;

import api.scheduling.tasks.*;
import java.util.HashSet;

public interface IAlgorithm {
    void add(HashSet<JobContext> runningJobs, JobContext job, Integer priority);
    void run(HashSet<JobContext> runningJobs, JobContext jobContext);
    JobContext remove();
    Integer count();

    default JobContext poll() { return remove(); }
    default Integer size() { return count(); }
}
