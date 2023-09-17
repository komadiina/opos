package api.scheduling.tasks.demo;

import api.scheduling.tasks.*;

import java.util.concurrent.*;
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
        System.out.printf("[DemoTask] Demo task '%1$s' started, parallelism degree: %2$d%n", name, parallelismDegree);
        Vector<Integer> data = Stream.iterate(0, x -> x + 1)
                .limit(numIterations)
                .collect(Collectors.toCollection(Vector::new));

        // Simulate work
        ExecutorService pool = Executors.newFixedThreadPool(parallelismDegree);
        List<Callable<Void>> tasks = new ArrayList<>();
        for (Integer i : data) {
            tasks.add(() -> {
                System.out.println(name + ": " + i);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }

        try {
            pool.invokeAll(tasks);
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[DemoTask] Demo task finished.");
    }
}
