package thnet.thanlib.com.httpsandroid;

/**
 * Created by yi on 2/23/16.
 */
public class LocData {
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return String.format("{ip: %s}", getIp());
    }
}
