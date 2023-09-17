package tests;

import api.scheduling.Scheduler;
import api.scheduling.algorithms.PriorityAlgorithm;
import api.scheduling.tasks.demo.*;
import api.scheduling.tasks.*;

public class TestPriorityScheduling {
    public static void main(String[] args) {
        System.out.println("--------------- TEST ---------------");
        System.out.println("- PriorityScheduling functionality -");
        System.out.println("------------------------------------");

        // Set up a FIFO scheduler
        Scheduler scheduler = new Scheduler();
        scheduler.setAlgorithm(new PriorityAlgorithm());

        // Create some demo tasks
        DemoTask task1 = new DemoTask("Task 1", 500, 20, 0);
        DemoTask task2 = new DemoTask("Task 2", 500, 20, 1);
        DemoTask task3 = new DemoTask("Task 3", 500, 20, 2);

        // Lower priority == executes first
        JobSpecification job1 = new JobSpecification(task1, 50);
        JobSpecification job2 = new JobSpecification(task2, 0);
        JobSpecification job3 = new JobSpecification(task3, 100);
        JobSpecification job4 = new JobSpecification(task1, 25);

        System.out.println("Scheduling tasks...");
        Job jobA = scheduler.schedule(job1);
        Job jobB = scheduler.schedule(job2);
        Job jobC = scheduler.schedule(job3);
        Job jobD = scheduler.schedule(job4);
        System.out.println("Tasks scheduled.");
    }
}
