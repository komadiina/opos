package tests;

import api.scheduling.Scheduler;
import api.scheduling.algorithms.FIFOAlgorithm;
import api.scheduling.tasks.demo.DemoTask;
import api.scheduling.tasks.JobSpecification;

public class TestDemoTask {
    public static void main(String[] args) {
        System.out.println("------------ TEST ------------");
        System.out.println("--- DemoTask functionality ---");
        System.out.println("------------------------------");

        // Set up a FIFO scheduler
        Scheduler scheduler = new Scheduler();
        scheduler.setAlgorithm(new FIFOAlgorithm());

        // Create some demo tasks
        DemoTask task1 = new DemoTask("Task 1", 500, 20, 0);
        DemoTask task2 = new DemoTask("Task 2", 500, 20, 1);
        DemoTask task3 = new DemoTask("Task 3", 500, 20, 2);
        JobSpecification job1 = new JobSpecification(task1, 0);
        JobSpecification job2 = new JobSpecification(task2, 0);
        JobSpecification job3 = new JobSpecification(task3, 0);

        System.out.println("Scheduling tasks...");
        scheduler.schedule(job1);
        scheduler.schedule(job2);
        scheduler.schedule(job3);
        System.out.println("Tasks scheduled.");
    }
}
