package badvision.dataanalysis.util;

import badvision.dataanalysis.model.GeoInfo;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

/**
 * Utility for working with geo location data
 * @author brobert
 */
public class GeoUtil {
    private GeoUtil() {
        // Utility class has no constructor
    }
    
    public static Optional<GeoInfo> geoGeoInfoForIP(String ip) {
        try {
            URL ipInfo = new URL("https://ipinfo.io/"+ip);
            GeoInfo info = JsonUtil.parseReaderAsClass(new InputStreamReader(ipInfo.openStream()), GeoInfo.class);
            return Optional.of(info);
        } catch (IOException ex) {
            System.err.println("Error looking up location for "+ip+": "+ex.getMessage());
            ex.printStackTrace(System.err);
        }
        return Optional.empty();
    }
    
}
