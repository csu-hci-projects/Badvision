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
    private int number;
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
    
    public String getTrialOrder() {
        StartRecord s = getStartRecord().orElseThrow();
        return s.getRequest().getTrialOrder();
    }

    public int getRestartCount() {
        return (int) (getRecordsOfType(StartRecord.class).count()-1);        
    }
    
    public Optional<StartRecord> getStartRecord() {
        return getRecordsOfType(StartRecord.class)
                .reduce((a,b)->b);
    }

    public Optional<EndRecord> getEndRecord() {
        return getRecordsOfType(EndRecord.class)
                .reduce((a,b)->b);
    }

    public void performSubjectAnalysis() {
        if (isComplete()) {
            getTrialRecords().forEach((TrialRecord r) -> r.getRequest().getBody().performTrialAnalysis());            
        }
    }
    
    public Optional<TrialRecord> getTrialRecord(int trial) {
        if (!isComplete()) {
            return Optional.empty();
        }
        String s = String.valueOf(trial);
        int idx = getTrialOrder().indexOf(s);
        return Optional.of(getTrialRecords().get(idx));
    }
    
    public boolean isComplete() {
        return getTrialRecords().size() == 4;
    }
}
