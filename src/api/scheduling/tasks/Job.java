package api.scheduling.tasks;

public class Job {
    private final JobContext jobContext;

    public Job(JobContext jobContext) {
        this.jobContext = jobContext;
    }

    public void jobWait() {
        jobContext.waitContext();
    }

    public void requestPause() {
        jobContext.requestPause();
    }

    public void requestContinue() {
        jobContext.requestContinue();
    }
}
