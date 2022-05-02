package badvision.dataanalysis.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author brobert
 */
@Data
public class MultuWayAnovaResults {
    private String factor1name;
    private String factor2name;
    private String[] factor1levels;
    private String[] factor2levels;
    private Map<String, AnovaResults> results = new HashMap();
}
