package badvision.dataanalysis.util;

import badvision.dataanalysis.model.GeoInfo;
import java.util.Calendar;

/**
 * String formatting utilities
 * @author brobert
 */
public class StringUtil {
    private StringUtil() {
        // Util class has no constructor
    }
    
    public static String formatDate(Calendar cal) {
        if (cal == null) {
            return "";
        } else {
            return cal.toInstant().toString();
        }
    }
    
    public static String formatLocation(GeoInfo geo) {
        if (geo == null) {
            return "";
        } else {
            return geo.getCity();
        }
    }    
    
}
