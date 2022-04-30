package badvision.dataanalysis.model;

import java.util.Calendar;
import java.util.List;
import lombok.Data;

/**
 * Prompt/typed response (5 per trial)
 * @author brobert
 */
@Data
public class Prompt {
    private Calendar start;
    private String phrase;
    private List<Keystroke> keystrokes;
    
}
