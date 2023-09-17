package tests;

import api.scheduling.Scheduler;
import api.scheduling.algorithms.*;
import api.scheduling.tasks.*;
import api.scheduling.tasks.demo.*;

public class TestFIFOScheduling {
    public static void main(String[] args) {
        System.out.println("----------- TEST -----------");
        System.out.println("--- FIFO/FCFS Scheduling ---");
        System.out.println("----------------------------");

        Scheduler scheduler = new Scheduler();
        scheduler.setAlgorithm(new FIFOAlgorithm());

        JobSpecification job1 = new JobSpecification(new DemoTask("Task1", 250, 20, 0), 0),
                    job2 = new JobSpecification(new DemoTask("Task2", 250, 20, 0), 0),
                    job3 = new JobSpecification(new DemoTask("Task3", 250, 20, 0), 0),
                    job4 = new JobSpecification(new DemoTask("Task4", 250, 20, 0), 0),
                    job5 = new JobSpecification(new DemoTask("Task5", 250, 20, 0), 0);

        System.out.println("FIFO/FCFS Scheduling tasks...");
        scheduler.schedule(job1);
        scheduler.schedule(job3);
        scheduler.schedule(job2);
        scheduler.schedule(job4);
        scheduler.schedule(job5);
        System.out.println("Tasks scheduled.");
    }
}
