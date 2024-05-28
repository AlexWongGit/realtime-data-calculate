package com.alex.demo.count;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.FloatSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

/**
 * @Author: Stephen
 * @Date: 2020/3/3 15:51
 * @Content: 模拟股市价格的成交价
 */
public class SendMsg {
    public static void main(String[] args) throws InterruptedException {
        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.56.122:9092");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FloatSerializer.class);
        Random rand = new Random();
        KafkaProducer<String,Float> prod = new KafkaProducer<String,Float>(prop);
        while (true){
            for (int i=0;i<50;i++){
                float lc = 30+20*rand.nextFloat();
                float lou = 4+rand.nextFloat();
                // 利欧股票成交价格
                ProducerRecord<String, Float> lousend = new ProducerRecord<String, Float>("模拟主题", "002131", lou);
                // 浪潮股票成交价格
                ProducerRecord<String, Float> lcsend = new ProducerRecord<String, Float>("模拟主题", "000977", lc);
                prod.send(lcsend);
                Thread.sleep(250);
                prod.send(lousend);
                Thread.sleep(250);
            }
            prod.flush();
        }
    }
}


