package badvision.dataanalysis.model;

import java.util.Calendar;
import lombok.Data;

/**
 * Generic request details for response body
 * @author brobert
 * @param <B> Body class type
 */
@Data
public abstract class RequestDetails<B> {
    private Calendar time;
    private B body;
    abstract public boolean isValidRecord();
}
