package badvision.dataanalysis.model;

import lombok.Data;

/**
 * Data parsed from request headers
 * @author brobert
 */
@Data
public class DeviceDetails {
    private String ip;
    private String userAgent;
    private String userHash;
    private DeviceDetails.ClientHints clientHints;
    
    @Data
    public static class ClientHints {
        private String agent;
        private String mobile;
        private String platform;
    }
}
