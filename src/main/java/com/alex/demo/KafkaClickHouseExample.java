package com.alex.demo;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class KafkaClickHouseExample {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-clickhouse-example");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream("input-topic");

        // 实时处理数据流，并将数据写入 ClickHouse
        stream.foreach((key, value) -> {
            // 将数据写入 ClickHouse
            insertDataIntoClickHouse(value);
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    private static void insertDataIntoClickHouse(String data) {
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setUser("alex");
        properties.setPassword("a123456");
        ClickHouseDataSource dataSource = new ClickHouseDataSource("jdbc:clickhouse://localhost:8123", properties);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO test_table VALUES (?)")) {
            statement.setString(1, data);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
