package stream_processing.Average_Power_Consumption;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import stream_processing.Common.*;

import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.api.java.functions.KeySelector;

public class Average_Power_Consumption {
    // help functions which check if value is empty before parsing
    private static Integer parseInteger(String value) {
        return value != null && !value.isEmpty() ? Integer.parseInt(value) : null;
    }
    
    private static Double parseDouble(String value) {
        return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
    }
    
    // main function that creates the stream environment, sets the data source and does the computations
    public static void main(String[] args) throws Exception {
        // uncomment the following if you wish to submit the job to the Apache Flink Dashboard
        //final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        // checkpoint every approximately 10 minutes, provided in milliseconds
        env.enableCheckpointing(601000); 

        // configurations for the CSV output format
        OutputFileConfig con = OutputFileConfig
        .builder()
        .withPartSuffix(".csv")
        .build();
        
        /* creates a file sink that writes aggregated data to a specified directory using the OnCheckpoint rolling policy 
        (the file will only be finalized when the specified checkpoint policy duration is over), 
        along with the previously defined output configurations for CSV output 
        inspired by: https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/connectors/datastream/filesystem/#file-sink
        */
        final FileSink<String> sink = FileSink
        .forRowFormat(new Path("path/to/output/directory/"), new SimpleStringEncoder<String>())
        .withRollingPolicy(OnCheckpointRollingPolicy.build())
        .withOutputFileConfig(con)
        .build();

        // configurations for the Kafka consumer that Flink will use to consume data from the Kafka topics 
        // https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/connectors/datastream/kafka/#kafka-sourcefunction
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");


        // list of topics
        List<String> topics = new ArrayList<>();
        // i start with 1, because there is no topic household0
        for(int i=1; i<10; i++) {
            topics.add("household" + i);
        }

        // create a stream for each topic, inspired by: https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/connectors/datastream/kafka/
        List<DataStream<String>> streams = new ArrayList<>();
        for(String topic : topics) {
            KafkaSource<String> source = KafkaSource
                .<String>builder()
                .setBootstrapServers("localhost:9092")
                .setGroupId("test")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setTopics(topic)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build(); 
            streams.add(env.fromSource(source, WatermarkStrategy.noWatermarks(), "KafkaSource"));
        }

        // union all the streams into one: https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/dev/datastream/operators/overview/#union
        DataStream<String> csvLines = streams.get(0);
        for(int i = 1; i < streams.size(); i++) {
            csvLines = csvLines.union(streams.get(i));
        }

        // map data from the csv to create household data object 
        DataStream<Reduced_hh_Data> energyData = csvLines.map(new MapFunction<String, Reduced_hh_Data>() {
            @Override
            public Reduced_hh_Data map(String value) {
                String[] parts = value.split(",");
                Reduced_hh_Data data = new Reduced_hh_Data();

                try {
                    data.setDate(parts[0]);
                    data.setTimestamp(Long.parseLong(parts[1]));
                    data.setEnergy(Long.parseLong(parts[2]));
                    data.setPower(Long.parseLong(parts[3]));
                    data.setUserId(parts[7]);
                    data.setNumberOfPeople(parseInteger(parts[8]));
                    data.setSquareMeters(parseDouble(parts[10]));
                } catch (Exception e) {
                    System.out.println(
                        "Exception occured in reduced_hh_data map: " + e.toString()
                    );
                }
                return data;
            }
        });
                
        // extract timestamps and generate watermarks: https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/dev/datastream/event-time/generating_watermarks/
        DataStream<Reduced_hh_Data> timestampedAndWatermarkedData = energyData
        .assignTimestampsAndWatermarks(
            WatermarkStrategy.<Reduced_hh_Data>forMonotonousTimestamps()
            .withTimestampAssigner((SerializableTimestampAssigner<Reduced_hh_Data>) (element, recordTimestamp) ->  element.getTimestamp()));

        // calculates the average energy consumption within a given time window, for each household
        // the output is: date, userId, peak timestamp, average power
        DataStream<Reduced_hh_Data> avgConsumptionData = timestampedAndWatermarkedData
        // organizes the data into groups (keys) based on userId 
        .keyBy(new KeySelector<Reduced_hh_Data, String>() {
            @Override
            public String getKey(Reduced_hh_Data value) {
                return value.getUserId() == null ? "unknown" : value.getUserId();
            }
        })
        // For each household this step defines a separate time window of 4 minutes
        // all data records with the same userId falling within that window interval will be processed together
        .window(TumblingEventTimeWindows.of(Time.minutes(4)))
        // find the average power consumption in that window for that household
        .process(new ProcessWindowFunction<Reduced_hh_Data, Reduced_hh_Data, String, TimeWindow>() {
            @Override
            public void process(String key, Context context, Iterable<Reduced_hh_Data> elements, Collector<Reduced_hh_Data> out) throws Exception {
                Avg_Data avgData = new Avg_Data();
                Reduced_hh_Data current = null;
                for (Reduced_hh_Data element : elements) {
                    avgData.sum += element.getPower();
                    avgData.count++;
                    current = element;
                }
                if (current != null) {
                    current.setAvg_power((long)avgData.sum / avgData.count);
                    out.collect(current);
                }
            }
        });

        // map aggregated data to the sink
        DataStream<String> output = avgConsumptionData.map(new MapFunction<Reduced_hh_Data, String>() {
            @Override
            public String map(Reduced_hh_Data value) {
                return value.avg_toCSV_String();
            }
        });

        // write the processed data to the specified sink with a parallelism level of 1
        // this means that one thread will be used for this operation, ensuring the output is written in a single file
        output.sinkTo(sink).setParallelism(1);
        //prints the contents of avgConsumptionData stream to the console, used for debugging and monitoring
        avgConsumptionData.print().setParallelism(1);
        // triggers the execution of the Flink data stream pipeline and the computations begin
        env.execute("simple average aggregation");
    }
}
