package com.alex.demo;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.StoreBuilder;
import java.util.Properties;

public class RealTimeStatistics {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "real-time-statistics");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> dataStream = builder.stream("input_topic");

        KTable<String, Long> dimensionMetrics = dataStream
                // 提取维度
                .selectKey((key, value) -> extractDimension(value))
                .groupByKey()
                .count(Materialized.as("dimension-metrics"));

        // 在这里可以添加更多的统计逻辑，如基于指标的聚合等

        dimensionMetrics.toStream().foreach((key, value) -> {
            // 将统计结果推送到前端可视化库
            // 将结果推送到前端展示
            // 可参考前端可视化库的文档，将数据用于生成柱状图、饼图等图表
            System.out.println("Dimension: " + key + ", Count: " + value);
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // 等待程序终止
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static String extractDimension(String data) {
        // 从数据中提取维度信息
        // 这里可以根据你的数据格式和需求进行维度信息提取
        // 假设提取的维度信息为 "dimension"
        return "dimension";
    }
}
