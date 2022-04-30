package badvision.dataanalysis.model;

import badvision.dataanalysis.model.RecordedResponse.EndRecord;
import badvision.dataanalysis.model.RecordedResponse.StartRecord;
import badvision.dataanalysis.model.RecordedResponse.TrialRecord;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;

/**
 * Subject experiment session data
 * @author brobert
 */
@Data
public class Subject {
    private String filename;
    private String ip;
    private GeoInfo location;
    private List<RecordedResponse> records = new ArrayList<>();
    
    public Calendar getStartTime() {
        return getStartRecord().map(e->e.getRequest().getTime()).orElse(null);
    }
    
    public Calendar getEndTime() {
        return getEndRecord().map(e->e.getRequest().getTime()).orElse(null);
    }
    
    public int getRecordCount() {
        return records.size();
    }
    
    private <C extends RecordedResponse> Stream<C> getRecordsOfType(Class<C> c) {
        return (Stream<C>) records.stream()
                .filter(r->c.isInstance(r));        
    }
    
    public List<TrialRecord> getTrialRecords() {
        return getRecordsOfType(TrialRecord.class)
                .collect(Collectors.toList());
    }

    public Optional<StartRecord> getStartRecord() {
        return getRecordsOfType(StartRecord.class)
                .reduce((a,b)->b);
    }

    public Optional<EndRecord> getEndRecord() {
        return getRecordsOfType(EndRecord.class)
                .reduce((a,b)->b);
    }
}
