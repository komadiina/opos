package api.scheduling.tasks.demo;

import api.scheduling.tasks.*;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.*;
import java.util.*;

public class DemoTask implements IUserJob {
    public String name;
    public Integer sleepTime, numIterations;
    public final Integer parallelismDegree; // No. of CPU cores to utilize

    public DemoTask(String name, Integer sleepTime, Integer numIterations, Integer parallelismDegree) {
        this.name = name;
        this.sleepTime = sleepTime;
        this.numIterations = numIterations;
        this.parallelismDegree = parallelismDegree;
    }

    @Override
    public void run(IJobContext jobContext) {
        System.out.printf("Demo task '%1$s' started, parallelism degree: %2$d%n", name, parallelismDegree);
        Vector<Integer> data = Stream.iterate(0, x -> x + 1)
                .limit(numIterations)
                .collect(Collectors.toCollection(Vector::new));

        // Simulate work
        if (parallelismDegree > 0)
            try (ForkJoinPool pool = new ForkJoinPool(parallelismDegree);) {
                data.parallelStream().forEach(x -> {
                    System.out.println(name + ": " + x);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IllegalArgumentException ex) {
                System.out.println("Parallelism degree must be positive.");
                ex.printStackTrace();
            }
        else data.stream().forEachOrdered(x -> {
            System.out.println(name + ": " + x);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Demo task finished.");
    }
}
