package api.scheduling.tasks.demo;

import api.scheduling.tasks.*;
import javax.sound.sampled.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class AudioGain implements IUserJob {
    public String name = "Audio gain task.";
    public Integer sleepTime = 0;
    public Integer numIterations = 1;
    public Integer gaindB = 0;
    public String filePath, fileDirectory, fileName, outputDirectory;
    public final Integer parallelismDegree; // No. of CPU cores to utilize

    public AudioGain(String name, Integer sleepTime, Integer numIterations,
                     Integer gainValue, String fileDirectory, String fileName,
                     String outputDirectory, Integer parallelismDegree) {
        this.name = name;
        this.sleepTime = sleepTime;
        this.numIterations = numIterations;
        this.gaindB = gainValue;

        this.fileDirectory = fileDirectory.endsWith(File.separator) ?
                fileDirectory :
                fileDirectory + File.separator;
        this.fileName = fileName;

        this.outputDirectory = outputDirectory.endsWith(File.separator) ?
                outputDirectory :
                outputDirectory + File.separator;

        this.filePath = this.fileDirectory + this.fileName;
        this.parallelismDegree = numIterations > parallelismDegree ?
                                  parallelismDegree : numIterations;
    }

    public void run(IJobContext jobContext) {
        assert filePath != null;

        System.out.println("Started AudioGain task...");

        // Read WAVE file
        File file = new File(filePath);
        AudioInputStream ais;
        try {
            ais = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            System.err.println("Could not read audio file: " + filePath);
            return;
        }

        // Header
        AudioFormat format = ais.getFormat();

        // Sanity checks
        assert format.getSampleSizeInBits() == 16;
        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED &&
                format.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED)
            throw new IllegalArgumentException("Only PCM encoded .WAV files are supported.");

        Vector<Integer> data = readData(ais, format.isBigEndian()), original = data;

        try (ForkJoinPool pool = new ForkJoinPool(parallelismDegree);) {
            for (int iteration = 1; iteration <= this.numIterations; iteration++) {
                System.out.println("Iteration #" + iteration);

                for (int i = 0; i < data.size(); i++)
                    data.set(i, attenuate(data.elementAt(i), gaindB));

                saveToFile(data,
                        (int) format.getSampleRate(),
                        String.format("%1$s%2$s_%3$s", outputDirectory, getCurrentTime(), fileName));
            }

            // Sanity check (yet again)
            pool.shutdown();
        } catch (IllegalArgumentException ex) {
            System.out.println("Parallelism degree must be positive.");
            ex.printStackTrace();
        }

        System.out.println("Finished AudioGain task...");
        try {
            ais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        String time = LocalDateTime.now().toString();
        time = time.replaceAll(":", "-");
        return time.replaceAll("\\.", "_");
    }

    public Vector<Integer> readData(AudioInputStream ais, boolean isBigEndian) {
        byte[] sampleBuffer = new byte[2];
        Vector<Integer> data = new Vector<Integer>();

        // Read data
        try {
            while (ais.read(sampleBuffer) != -1) {
                int sample;
                if (isBigEndian) {
                    sample = (int)sampleBuffer[0] << 8 | (int)sampleBuffer[1];
                } else {
                    sample = (int)sampleBuffer[1] << 8 | (int)sampleBuffer[0];
                }
                data.add(sample);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return data;
    }

    public void saveToFile(Vector<Integer> data, Integer sampleRate, String filePath) {
        try {
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sampleRate, 16, 1, 2, sampleRate, false);
            File outputFile = new File(filePath);
            ByteArrayInputStream bais = new ByteArrayInputStream(toByteArray(data));
            AudioInputStream ais = new AudioInputStream(bais, format, data.size());
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Could not write audio file: " + filePath);
        }
    }

    public static Integer attenuate(Integer value, Integer gaindB) {
//        System.out.println((int)(value * Math.pow(10, gaindB / 20.0)));
        return (int)(value * Math.pow(10, gaindB / 20.0));
    }

    private static byte[] toByteArray(Vector<Integer> samples) {
        byte[] byteArray = new byte[samples.size() * 2]; // 16-bit samples, 2 bytes per sample
        for (int i = 0; i < samples.size(); i++) {
            int sample = samples.get(i);
            byteArray[i * 2] = (byte) (sample & 0xFF);
            byteArray[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return byteArray;
    }
}
