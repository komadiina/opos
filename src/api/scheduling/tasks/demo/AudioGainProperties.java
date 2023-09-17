package api.scheduling.tasks.demo;

import java.util.List;

public class AudioGainProperties {
    public static Integer gaindBMin = -30;
    public static Integer gaindBMax = 30;
    public static Integer numIterationsMin = 1;
    public static Integer numIterationsMax = 100;
    public static Integer sleepTimeMin = 0;
    public static Integer sleepTimeMax = 5000;
    public static Integer parallelismDegreeMin = 1;
    public static Integer parallelismDegreeMax = 4;

    public Integer gaindB;
    public Integer numIterations;
    public Integer sleepTime;
    public Integer parallelismDegree;

    public Integer priority;

    public AudioGainProperties(Integer gaindB, Integer numIterations, Integer sleepTime, Integer parallelismDegree) {
        this.gaindB = gaindB;
        this.numIterations = numIterations;
        this.sleepTime = sleepTime;
        this.parallelismDegree = parallelismDegree;

        verify();
    }

    public AudioGainProperties(List<String> parsedLines) {
        String[] gaindBLine = parsedLines.get(0).split("=");
        String[] numIterationsLine = parsedLines.get(1).split("=");
        String[] sleepTimeLine = parsedLines.get(2).split("=");
        String[] parallelismDegreeLine = parsedLines.get(3).split("=");


        this.gaindB = Integer.parseInt(gaindBLine[1]);
        this.numIterations = Integer.parseInt(numIterationsLine[1]);
        this.sleepTime = Integer.parseInt(sleepTimeLine[1]);
        this.parallelismDegree = Integer.parseInt(parallelismDegreeLine[1]);

        verify();
    }

    private void verify() {
        if (gaindB < gaindBMin) {
            gaindB = gaindBMin;
            System.err.println("Gain value too low, setting to minimum value: " + gaindBMin);
        } else if (gaindB > gaindBMax) {
            gaindB = gaindBMax;
            System.err.println("Gain value too high, setting to maximum value: " + gaindBMax);
        }

        if (numIterations < numIterationsMin) {
            numIterations = numIterationsMin;
            System.err.println("Number of iterations too low, setting to minimum value: " + numIterationsMin);
        } else if (numIterations > numIterationsMax) {
            numIterations = numIterationsMax;
            System.err.println("Number of iterations too high, setting to maximum value: " + numIterationsMax);
        }

        if (sleepTime < sleepTimeMin) {
            sleepTime = sleepTimeMin;
            System.err.println("Sleep time too low, setting to minimum value: " + sleepTimeMin);
        } else if (sleepTime > sleepTimeMax) {
            sleepTime = sleepTimeMax;
            System.err.println("Sleep time too high, setting to maximum value: " + sleepTimeMax);
        }

        if (parallelismDegree < parallelismDegreeMin) {
            parallelismDegree = parallelismDegreeMin;
            System.err.println("Parallelism degree too low, setting to minimum value: " + parallelismDegreeMin);
        } else if (parallelismDegree > parallelismDegreeMax) {
            parallelismDegree = parallelismDegreeMax;
            System.err.println("Parallelism degree too high, setting to maximum value: " + parallelismDegreeMax);
        }
    }
}
