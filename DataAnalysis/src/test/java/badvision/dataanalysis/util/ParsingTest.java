package badvision.dataanalysis.util;

import badvision.dataanalysis.model.DeviceDetails;
import badvision.dataanalysis.model.RecordedResponse.StartRecord;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Some basic tests to ensure json parsing is working at some basic level.
 * Because it's all provided by pattern, not everything is asserted here because
 * ultimately it doesn't need to be.
 * @author brobert
 */
public class ParsingTest {
   
    @Test
    public void testHeaderParsing() {
        String str = "{\"ip\":\"1.1.1.1\","
                + "\"userAgent\":\"user agent here\","
                + "\"userHash\":\"hash-code\","
                + "\"clientHints\":{\"agent\":\"NONE\",\"mobile\":\"NONE\",\"platform\":\"NONE\"}}";
        DeviceDetails dev = JsonUtil.parseStringAsClass(str, DeviceDetails.class);
        assertEquals("1.1.1.1", dev.getIp());
        assertEquals("user agent here", dev.getUserAgent());
        assertEquals("hash-code", dev.getUserHash());
        assertEquals("NONE", dev.getClientHints().getAgent());
        assertEquals("NONE", dev.getClientHints().getMobile());
        assertEquals("NONE", dev.getClientHints().getPlatform());
    }
    
    @Test
    public void testStartParsing() {
        String str = "{\"device\":{"
                + "\"ip\":\"1.1.1.1\","
                + "\"userAgent\":\"user agent here\","
                + "\"userHash\":\"hash-code\","
                + "\"clientHints\":{\"agent\":\"NONE\",\"mobile\":\"NONE\",\"platform\":\"NONE\"}"
                + "},\"request\":{"
                        + "\"time\":\"2022-04-24T00:30:10.123Z\","
                        + "\"query\":\"\","
                        + "\"body\":{"
                        + "\"age\":\"7\","
                        + "\"device\":\"ipad\","
                        + "\"device-other\":\"\","
                        + "\"position\":\"table\","
                        + "\"impairment\":\"none\","
                        + "\"impairment-other\":\"\""
                + "},"
                + "\"trialOrder\":\"0132\"}},";
        StartRecord s = JsonUtil.parseStringAsClass(str, StartRecord.class);
        assertEquals("0132", s.getRequest().getTrialOrder());
    }
}
