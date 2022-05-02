package badvision.dataanalysis.model;

import badvision.dataanalysis.util.AnalysisUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.Data;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Prompt/typed response (5 per trial)
 * @author brobert
 */
@Data
public class Prompt {
    private Calendar start;
    private String phrase;
    private List<Keystroke> keystrokes;
    private int totalCorrect = 0;
    private int totalSkipped = 0;
    private int totalIncorrect = 0;
    private double accuracy;
    private double wpm;
    private double speedMedian;
    private double speedStd;
    private List<Double> correctKeystrokeTimes = new ArrayList<>();
    
    /**
     * Calculate accuracy, WPM, and timing-related statistics for a single prompt
     */
    public void performPromptAnalysis() {
        Calendar lastCorrectTime = start;
        correctKeystrokeTimes.clear();        
        for (int i=0; i < keystrokes.size(); i++) {
            Keystroke k = keystrokes.get(i);
            if (k == null || k.getType() == null) {
                continue;
            }
            switch (k.getType()) {
                case correct:
                    correctKeystrokeTimes.add((double) k.getTime().getTimeInMillis() - lastCorrectTime.getTimeInMillis());
                    lastCorrectTime = k.getTime();
                    totalCorrect++;
                    break;
                case incorrect:
                    totalIncorrect++;
                    break;
                case skip:
                    totalSkipped++;
                    break;
            }
        }
        // Accuracy is the percentage of correctly typed keys, and multiple mistakes per key count in addition.
        accuracy = ((double) phrase.length() - (double) totalIncorrect - (double) totalSkipped) / (double) phrase.length();
        // WPM calculation basd on average word size is 5 including spaces and punctuation
        double words = phrase.length() / 5.0;
        double minutes = ((double) (lastCorrectTime.getTimeInMillis() - start.getTimeInMillis()))/60000.0;
        wpm = words / minutes;
        // Speed median and std. dev is based on time per correct keystroke
        DescriptiveStatistics stats = new DescriptiveStatistics(AnalysisUtil.toNativeArray(correctKeystrokeTimes));
        speedMedian = stats.getMean();
        speedStd = stats.getStandardDeviation();
    }
}
