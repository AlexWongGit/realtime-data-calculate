package com.alex.demo.count;


import com.alex.demo.count.Agg;
import com.alex.demo.count.AggDeseriallizer;
import com.alex.demo.count.AggSeriallizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: Stephen
 * @Date: 2020/3/3 16:27
 * @Content: 利用流统计每种股票价格
 *
 * 这段代码是一个基于 Kafka Streams 的实时数据处理程序。
 * 它从名为 “play01” 的 Kafka 主题中读取消息，然后按照特定的窗口时间（2秒）进行计算聚合，并将结果写入到 Redis 中。
 * 其中关键部分的功能：
 *
 * 配置 Kafka Streams 相关属性：设置 Kafka 服务器地址、应用程序ID、键和值的序列化方式等。
 *
 * 创建一个流处理器构建器 StreamsBuilder。
 *
 * 从 “play01” 主题创建 KStream 流。
 * 使用 groupByKey()、windowedBy()、aggregate() 方法对流进行分组、窗口化和聚合处理，计算每个窗口内的数据总和、计数和平均值，
 * 并将结果写入到名为 “tmp-stream-store” 的状态存储中。
 *
 * 使用 Jedis 连接 Redis 数据库，并将计算结果以特定的格式写入到 Redis 中。
 *
 * 创建流处理拓扑（Topology），构建 KafkaStreams 对象并启动流处理任务。
 *
 * 添加一个关闭钩子，确保程序在关闭时正确关闭流处理任务。
 *
 * 总体来说，这段代码实现了一个简单的实时数据处理应用，从 Kafka 主题读取数据，进行窗口化计算，并将结果写入 Redis 中。需要注意的是，代码中使用了 Jedis 库来连接 Redis，并使用 ObjectMapper 来序列化数据。
 */
public class CountPrice {

    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.56.122:9092");
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"play2");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.Float().getClass());
        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        // prop.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG,0);
        final StreamsBuilder builder = new StreamsBuilder();
        // 计算的拓扑结构
        KStream<String,Float> play01 = builder.stream("play01");
        KTable<Windowed<String>, Agg> tab = play01.groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(2)))
                .aggregate(
                        new Initializer<Agg>() {
                            @Override
                            public Agg apply() {
                                return new Agg();
                            }
                        },
                        new Aggregator<String, Float, Agg>() {
                            @Override
                            public Agg apply(String key, Float newValue, Agg aggValue) {
                                // System.out.println("当前窗口:"+key+".....新值："+newValue+".....上次的值："+aggValue);
                                float cnum = aggValue.getSum()+newValue;
                                aggValue.setSum(cnum);
                                aggValue.setCount(aggValue.getCount()+1);
                                aggValue.setAvg(cnum/(aggValue.getCount()));
                                return aggValue;
                            }
                        },
                        Materialized.<String,Agg, WindowStore<Bytes,byte[]>>as("tmp-stream-store")
                                .withValueSerde(Serdes.serdeFrom(new AggSeriallizer(),new AggDeseriallizer()))
                );
        final Jedis jedis = new Jedis("192.168.56.122");
        final ObjectMapper om = new ObjectMapper();
        tab.toStream().foreach((k,v)->
                {
                    try {
                        jedis.rpush("gp2","{\"shareid\":\""+k.key()+"\",\"infos\":"+om.writeValueAsString(v)
                                +",\"timestamp\":\""+k.toString().substring(22,35)+"\"}");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    //System.out.println(k.toString().replaceAll("",""));
                }
        );


        final Topology topo = builder.build();
        final KafkaStreams streams = new KafkaStreams(topo, prop);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("hw"){
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


