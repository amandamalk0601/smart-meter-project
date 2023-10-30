package stream_processing.Common;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Simple_CSV_Reader implements SourceFunction<String> {
    private String filePath;
    private boolean running = true;

    public Simple_CSV_Reader(String filePath) {
        this.filePath = filePath;
    }

    // function which reads the csv file line by line and collects the data
    @Override
    public void run(SourceContext<String> sourceContext) throws IOException {
        File dir = new File(filePath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    // skip header
                    String line = br.readLine();

                    // read next line
                    line = br.readLine();
                    while (running && line != null) {
                        synchronized (sourceContext.getCheckpointLock()) {
                            sourceContext.collect(line);
                            line = br.readLine();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
