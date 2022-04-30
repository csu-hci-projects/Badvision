package badvision.dataanalysis.model;

import java.util.Calendar;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    }
    
    @Override
    public boolean isValidRecord() {
        return getBody().getPrompts() != null && getBody().getPrompts().size() > 0;
    }
    
}
