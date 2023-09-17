package tests;

import api.scheduling.Scheduler;
import api.scheduling.algorithms.PriorityAlgorithm;
import api.scheduling.tasks.*;
import api.scheduling.tasks.demo.AudioGain;
public class TestAudioGain {
    public static void main(String[] args) {
        System.out.println("------------- TEST ------------");
        System.out.println("--- AudioGain functionality ---");
        System.out.println("-------------------------------");

        Scheduler scheduler = new Scheduler();
        scheduler.setAlgorithm(new PriorityAlgorithm());

        // Create some demo tasks
        JobSpecification job1 = new JobSpecification(
                new AudioGain(
                        "AudioGain 1",
                        0,
                        5,
                        15,
                        "./resources/input/", "untitled.wav",
                        "./resources/output/", 0), 0);
        JobSpecification job2 = new JobSpecification(
                new AudioGain(
                        "AudioGain 2",
                        0,
                        5,
                        -15,
                        "./resources/input/", "untitled.wav",
                        "./resources/output/", 0), 0);
        JobSpecification job3 = new JobSpecification(
                new AudioGain(
                        "AudioGain 3",
                        0,
                        5,
                        0,
                        "./resources/input/", "untitled.wav",
                        "./resources/output/", 0), 0);

        System.out.println("Scheduling tasks...");
        scheduler.schedule(job1);
        scheduler.schedule(job2);
        scheduler.schedule(job3);
        System.out.println("Tasks scheduled.");
    }
}
