package api.scheduling.tasks.demo;

import api.scheduling.tasks.*;

public class ExampleConsumers {
    public static void ConsumeFinishedJob(JobContext job) {
        System.out.println("Job finished.");
    }

    public static void ConsumePausedJob(JobContext job) {
        System.out.println("Job paused.");
    }

    public static void ConsumeResumedJob(JobContext job) {
        System.out.println("Job resumed.");
    }

    public static void ConsumeStartedJob(JobContext job) {
        System.out.println("Job started.");
    }
}
