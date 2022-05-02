package badvision.dataanalysis.model;

import java.util.List;
import lombok.Data;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Anova analysis results
 * @author brobert
 */
@Data
public class AnovaResults {
    private double pValue;
    private double fValue;
    private List<DescriptiveStatistics> seriesStatistics;    
}
