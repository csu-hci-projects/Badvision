package badvision.dataanalysis.model;

import java.util.Calendar;
import lombok.Data;

/**
 * Log info for single keystroke
 * @author brobert
 */
@Data
public class Keystroke {
    public static enum StrokeType {
        correct, incorrect, skip;
    }
    
    private Calendar time;
    private String key;
    private StrokeType type;
}
