package stream_processing.Peak_Energy_Consumption;

import stream_processing.Common.Reduced_hh_Data;
// extends Reduced_hh_Data to include window start and end times, specifically for peak power consumption within a time window
public class Window_Data_Peak extends Reduced_hh_Data{
    private long windowStart;
    private long windowEnd;

    public Window_Data_Peak(String date, long timestamp, long windowStart, long windowEnd, String userId, long power) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.setDate(date);
        this.setTimestamp(timestamp);
        this.setUserId(userId);
        this.setPower(power);
    }
    

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

    @Override
    public String peak_toCSV_String() {
        return super.peak_toCSV_String() + ", " + windowStart + ", " + windowEnd;
    }
}
