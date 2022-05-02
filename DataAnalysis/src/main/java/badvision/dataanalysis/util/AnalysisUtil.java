package badvision.dataanalysis.util;

import badvision.dataanalysis.model.AnovaResults;
import badvision.dataanalysis.model.MultuWayAnovaResults;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.OneWayAnova;

/**
 * Set of statistical analysis functions, mostly relying on Apache Commons Math
 * for the actual statistics calculations.
 * @author brobert
 */
public class AnalysisUtil {
   private AnalysisUtil() {
       // Utility class has no constructor
   } 

   /**
    * This isn't strictly a full two-way anova but rather a combination of one-way anovas using
    * various groupings.This allows to evaluate the independent variables more independently.
    * @param series Collection of subjects which are a collection of rows; assumes order of factors is A1B1,A2B1,A1B2,A2B2 where A and B are the two factors
     * @param factors The names of the independent variables (aka factors)
     * @param factorLevels Array of the levels for each of the factors
    * @return 
    */
   public static MultuWayAnovaResults performMultipleAnova(List<List<Double>>  series, String[] factors, String[]... factorLevels) {
       MultuWayAnovaResults res = new MultuWayAnovaResults();
       // For now we just assert that only 2 factors are supported.  Not going to boil the ocean here, this isn't a general-purpose library.
       assert(factors.length == 2);
       assert(factorLevels.length == 2);
       res.setFactor1name(factors[0]);
       res.setFactor2name(factors[1]);
       res.setFactor1levels(factorLevels[0]);
       res.setFactor2levels(factorLevels[1]);
       List<List<Double>> testSeries = new ArrayList<>();
       testSeries.add(mergeCollections(series.get(0), series.get(2)));
       testSeries.add(mergeCollections(series.get(1), series.get(3)));
       AnovaResults res1 = performOneWayAnova(testSeries);
       res.getResults().put(factors[1]+";combined", res1);
       //---
       testSeries.clear();
       testSeries.add(mergeCollections(series.get(0), series.get(1)));
       testSeries.add(mergeCollections(series.get(2), series.get(3)));
       AnovaResults res2 = performOneWayAnova(testSeries);
       res.getResults().put(factors[0]+";combined", res2);
       //---
       testSeries.clear();
       testSeries.add(mergeCollections(series.get(0), series.get(3)));
       testSeries.add(mergeCollections(series.get(1), series.get(2)));
       AnovaResults resNull = performOneWayAnova(testSeries);
       res.getResults().put("null", resNull);
       //---
       res.getResults().put(factors[0]+";"+factorLevels[1][0], performOneWayAnova(selectTrials(series, 0, 1)));
       res.getResults().put(factors[0]+";"+factorLevels[1][1], performOneWayAnova(selectTrials(series, 2, 3)));
       res.getResults().put(factors[1]+";"+factorLevels[0][0], performOneWayAnova(selectTrials(series, 0, 2)));
       res.getResults().put(factors[1]+";"+factorLevels[0][1], performOneWayAnova(selectTrials(series, 1, 3)));
       return res;
   }
   
   public static List<List<Double>> selectTrials(List<List<Double>> series, int t1, int t2) {
       List<List<Double>> res = new ArrayList<>();
       res.add(series.get(t1));
       res.add(series.get(t2));
       return res;
   }
   
   public static <A> List<A> mergeCollections(Collection<A> a, Collection<A> b) {
       ArrayList<A> result = new ArrayList<>();
       result.addAll(a);
       result.addAll(b);
       return result;
   }
   
   public static AnovaResults performOneWayAnova(List<List<Double>>  series) {
       List<double[]> stats = series.stream()
               .map(AnalysisUtil::toNativeArray)
               .collect(Collectors.toList());
       OneWayAnova anova = new OneWayAnova();
       AnovaResults results = new AnovaResults();
       results.setPValue(anova.anovaPValue(stats));
       results.setFValue(anova.anovaFValue(stats));
       
       results.setSeriesStatistics(
            series.stream()
               .map(AnalysisUtil::toNativeArray)
               .map(DescriptiveStatistics::new)
               .collect(Collectors.toList())
       );
       return results;
   }
   
   public static double[] toNativeArray(Double[] dd) {
       double[] d = new double[dd.length];
       for (int i=0; i < dd.length; i++) {
           d[i]=dd[i];
       }
       return d;
   }
   public static double[] toNativeArray(Collection<Double> dd) {
       double[] d = new double[dd.size()];
       Iterator<Double> iter = dd.iterator();
       for (int i=0; iter.hasNext(); i++) {
           d[i]=iter.next();
       }
       return d;
   }
}
