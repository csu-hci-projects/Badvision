package badvision.dataanalysis.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Record from start of experiment with opening questionnaire.
 * Also identifies the order of factors for subject
 * @author brobert
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StartRecordDetails extends RequestDetails<StartRecordDetails.StartRecordBody> {
    private String trialOrder;
    
    @Data
    public static class StartRecordBody {
        private String age;
        private String device;
        @SerializedName("deviceOther")
        private String deviceOther;
        private String position;
        private String impairment;
        @SerializedName("impairmentOther")
        private String impairmentOther;
    }

    @Override
    public boolean isValidRecord() {
        return trialOrder != null;
    }
}
