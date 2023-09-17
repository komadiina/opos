package api.scheduling.filesystem;

import api.scheduling.Scheduler;
import api.scheduling.tasks.JobSpecification;
import api.scheduling.tasks.demo.AudioGain;
import api.scheduling.tasks.demo.AudioGainProperties;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class FileWatcher extends Thread {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator +
                    "logs" + File.separator + "FSService.log");
            Logger.getLogger(FileWatcher.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public final String directory;
    private Path dirPath = null;
    public WatchService watcher;
    public final Scheduler scheduler;
    public boolean isRunning = true;

    public FileWatcher(Scheduler scheduler, String directory, WatchService watcher) {
        this.scheduler = scheduler;
        this.directory = directory;
        this.watcher = watcher;
        try {
            this.dirPath = Path.of(directory);
            this.dirPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException ex) {
            Logger.getLogger(FileWatcher.class.getName()).log(Level.SEVERE,
                    String.format("Could not register FSS for directory %1$s.%n", this.directory),
                    ex);
        }
    }

    @Override
    public void run() {
        System.out.println("[FS] Filesystem service started, directory: " + directory);

        // Schedule existing files
        try {
            Files.list(Paths.get(directory)).forEach(
                    file -> {
                        try {
                            AudioGainProperties properties = new AudioGainProperties(
                                    Files.readAllLines(Paths.get("./resources/settings.cfg")));
                            JobSpecification job = new JobSpecification(new AudioGain(
                                    "WatchService_AudioGainTask",
                                    properties.sleepTime,
                                    properties.numIterations,
                                    properties.gaindB,
                                    directory, file.getFileName().toString(),
                                    "./resources/output/", properties.parallelismDegree), 0);

                            System.out.println("[FS] Scheduling job for file: " + file.getFileName().toString());
                            scheduler.schedule(job);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (isRunning) {
            try {
                WatchKey key = watcher.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        // Collect specification
                        String filename = event.context().toString();
                        String filepath = Paths.get(directory, filename).toString();
                        System.out.println("[FS] New file detected: " + filepath);

                        // Sanity check
                        if (!filename.endsWith(".wav")) {
                            System.out.println("[FS] File is not a .wav file. Skipping...");
                        }
                        else {
                            AudioGainProperties properties = new AudioGainProperties(
                                    Files.readAllLines(Paths.get("./resources/settings.cfg")));

                            // Create a new job
                            JobSpecification jobSpec = new JobSpecification(new AudioGain(
                                    "WatchService_AudioGainTask",
                                    properties.sleepTime,
                                    properties.numIterations,
                                    properties.gaindB,
                                    directory, filename,
                                    "./resources/output/", properties.parallelismDegree), 0);

                            scheduler.schedule(jobSpec);
                        }
                    }
                }

                key.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        System.out.println("[FS] Exiting...");
    }
}
