package api.scheduling.tasks;

import java.util.Date;

public class JobSpecification {
    public IUserJob job;
    public Integer priority = null;

    public Date startTime = null;
    public Date endTime = null;
    public Long elapsedMillis = 0L;

    public JobSpecification(IUserJob job) {
        this.job = job;
    }

    public JobSpecification(IUserJob job, Integer priority) {
        this.job = job;
        this.priority = priority;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public IUserJob getJob() {
        return job;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }
}
