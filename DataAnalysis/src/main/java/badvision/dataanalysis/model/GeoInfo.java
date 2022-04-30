package badvision.dataanalysis.model;

import lombok.Data;

/**
 * Geo location info for IP addresses obtained from ipinfo.io
 * @author brobert
 */
@Data
public class GeoInfo {
    private String ip;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String loc;
    private String org;
    private String postal;
    private String timezone;
    private String readme;
}
