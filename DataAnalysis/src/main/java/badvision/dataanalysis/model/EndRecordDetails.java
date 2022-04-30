package badvision.dataanalysis.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Record from end of experiment with final questionnaire
 * @author brobert
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EndRecordDetails extends RequestDetails<EndRecordDetails.EndRecordBody> {

    @Override
    public boolean isValidRecord() {
        return getBody().getPhysical() != null || getBody().likeMetropolis != null || getBody().feedback != null;
    }
    
    @Data
    public static class EndRecordBody {
        private Integer likeMetropolis;
        private Integer physical;
        private String feedback;        
    }
    
    
}
