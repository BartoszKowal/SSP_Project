package pl.edu.agh.kt;
import java.util.Date;

public class SynEvent {
    private String sourceIpAddress;
    private Date timestamp;

    public SynEvent(String sourceIpAddress, Date timestamp) {
        this.sourceIpAddress = sourceIpAddress;
        this.timestamp = timestamp;
    }

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // Add toString() method if needed for logging
    @Override
    public String toString() {
        return "SynEvent{" +
                "sourceIpAddress='" + sourceIpAddress + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
