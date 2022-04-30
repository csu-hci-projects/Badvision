package badvision.dataanalysis.model;

import badvision.dataanalysis.util.JsonUtil;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base class for all recorded responses, cannot be directly instantiated
 *
 * @author brobert
 * @param <R> Request type for this record
 */
@Data
public abstract class RecordedResponse<R extends RequestDetails> {

    private DeviceDetails device;
    private String recordType;
    private R request;

    public static enum RecordTypes {
        start(StartRecord.class), trial(TrialRecord.class), end(EndRecord.class);
        Class<? extends RecordedResponse> clazz;

        RecordTypes(Class<? extends RecordedResponse> c) {
            clazz = c;
        }
    }

    public static Optional<RecordedResponse> fromString(String str) {
        str = str.trim();
        if (str.isEmpty()) {
            return Optional.empty();
        }
        for (RecordTypes r : RecordTypes.values()) {
            try {
                RecordedResponse rr = JsonUtil.parseStringAsClass(str, r.clazz);
                if (rr.getRequest().isValidRecord()) {
                    return Optional.of(rr);
                }
            } catch (Exception e) {
                System.err.println("Could not parse as "+r.name()+"; error was "+e.getMessage()+"\n"+str);
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class StartRecord extends RecordedResponse<StartRecordDetails> {

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TrialRecord extends RecordedResponse<TrialRecordDetails> {
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class EndRecord extends RecordedResponse<EndRecordDetails> {
    }

}
