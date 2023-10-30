package stream_processing.Common;
// extends Reduced_hh_Data to include window start and end times, representing data within a specific time window
public class Window_Data extends Reduced_hh_Data {
    private long windowStart;
    private long windowEnd;

    public long getWindowStart() {
        return windowStart;
    }
    public long getWindowEnd() {
        return windowEnd;
    }
    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }
    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }
}
