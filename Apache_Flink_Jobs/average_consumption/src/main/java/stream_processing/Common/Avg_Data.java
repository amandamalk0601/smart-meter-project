package stream_processing.Common;
// extends Reduced_hh_Data to track the sum and count of power readings for calculating average power consumption
public class Avg_Data extends Reduced_hh_Data{
    public double sum = 0;
    public long count = 0;

    public double getSum() {
        return sum;
    }
    public void setSum(double sum) {
        this.sum = sum;
    }
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }
}