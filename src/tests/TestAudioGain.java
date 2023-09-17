package tests;

import api.scheduling.Scheduler;
import api.scheduling.algorithms.FIFOAlgorithm;
import api.scheduling.algorithms.PriorityAlgorithm;
import api.scheduling.tasks.*;
import api.scheduling.tasks.demo.AudioGain;

import java.util.Arrays;

public class TestAudioGain {
    public static void main(String[] args) {
        System.out.println("------------- TEST ------------");
        System.out.println("--- AudioGain functionality ---");
        System.out.println("-------------------------------");

        Scheduler scheduler = new Scheduler();
        scheduler.setAlgorithm(new FIFOAlgorithm());

        // Create some example tasks (untitled.wav, untitled2.wav)
        JobSpecification job1 = new JobSpecification(
                new AudioGain(
                        "AudioGain 1",
                        0,
                        1,
                        15,
                        "./resources/input/", "untitled.wav",
                        "./resources/output/", 1), 0);
        JobSpecification job2 = new JobSpecification(
                new AudioGain(
                        "AudioGain 2",
                        0,
                        5,
                        -15,
                        "./resources/input/", "untitled.wav",
                        "./resources/output/", 2), 0);
        JobSpecification job3 = new JobSpecification(
                new AudioGain(
                        "AudioGain 3",
                        0,
                        5,
                        0,
                        "./resources/input/", "untitled.wav",
                        "./resources/output/", 4), 0);
        JobSpecification job4 = new JobSpecification(
                new AudioGain(
                        "AudioGain 4",
                        0,
                        5,
                        10,
                        "./resources/input/", Arrays.asList("untitled.wav", "untitled2.wav"),
                        "./resources/output/", 2), 0);


        System.out.println("[MAIN] Scheduling tasks...");
        scheduler.schedule(job1);
        scheduler.schedule(job2);
        scheduler.schedule(job3);
        scheduler.schedule(job4);
        System.out.println("[MAIN] Tasks scheduled.");
    }
}
