package badvision.dataanalysis.model;

import badvision.dataanalysis.util.AnalysisUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Details for one of the four experiment trials for a subject
 * @author brobert
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrialRecordDetails extends RequestDetails<TrialRecordDetails.TrialRecordBody> {
    @Data
    public static class TrialRecordBody {
        private Calendar start;
        private List<Prompt> prompts;
        private double timePerKeystrokeMean;
        private double timePerKeystrokeStd;
        private double wpmMean;
        private double wpmStd;
        private double accuracyMean;
        private double accuracyStd;

        public void performTrialAnalysis() {
            List<Double> allCorrectTimes = new ArrayList<>();            
            List<Double> allWpm = new ArrayList();
            List<Double> allAccuracy = new ArrayList();
            prompts.forEach(p->{
                p.performPromptAnalysis();
                allCorrectTimes.addAll(p.getCorrectKeystrokeTimes());
                allWpm.add(p.getWpm());
                allAccuracy.add(p.getAccuracy());
            });
            DescriptiveStatistics timeStats = new DescriptiveStatistics(AnalysisUtil.toNativeArray(allCorrectTimes));
            DescriptiveStatistics wpmStats = new DescriptiveStatistics(AnalysisUtil.toNativeArray(allWpm));
            DescriptiveStatistics accuracyStats = new DescriptiveStatistics(AnalysisUtil.toNativeArray(allAccuracy));
            timePerKeystrokeMean=timeStats.getMean();
            timePerKeystrokeStd=timeStats.getStandardDeviation();
            wpmMean=wpmStats.getMean();
            wpmStd=wpmStats.getStandardDeviation();
            accuracyMean=accuracyStats.getMean();
            accuracyStd=accuracyStats.getStandardDeviation();
        }
    }
    
    @Override
    public boolean isValidRecord() {
        return getBody().getPrompts() != null && getBody().getPrompts().size() > 0;
    }
    
}
