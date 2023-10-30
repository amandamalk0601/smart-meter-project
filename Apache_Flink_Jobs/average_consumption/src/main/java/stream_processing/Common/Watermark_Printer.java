package stream_processing.Common;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class Watermark_Printer extends ProcessFunction<Reduced_hh_Data, Reduced_hh_Data> {
    
    @Override
    public void processElement(Reduced_hh_Data value, Context ctx, Collector<Reduced_hh_Data> out) {
        System.out.println("Current watermark: " + ctx.timerService().currentWatermark());
        System.out.println("this is the date: " + value.getDate() + " timestamp: " + value.getTimestamp());
        out.collect(value);
    }
}
