package tests;

import api.scheduling.Scheduler;
import api.scheduling.filesystem.FileWatcher;
import api.scheduling.algorithms.FIFOAlgorithm;

import java.io.IOException;
import java.nio.file.*;

public class TestFileSystem {
    public static void main(String[] args) {
        System.out.println("-------------- TEST -------------");
        System.out.println("---- FileSystem functionality ---");
        System.out.println("---------------------------------");

        Scheduler scheduler = new Scheduler();
        scheduler.setAlgorithm(new FIFOAlgorithm());

        System.out.println("FIFO Scheduler initialized...");

        FileWatcher watcher;
        try {
            watcher = new FileWatcher(scheduler, Path.of("./resources/input/").toString(),
                FileSystems.getDefault().newWatchService());

            System.out.println("Starting file watcher...");
            watcher.start();
            System.out.println("File watcher started.");
        } catch (IOException ex) {
            System.err.println("Could not register filesystem service.");
        }
    }
}
