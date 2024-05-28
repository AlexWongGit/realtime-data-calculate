package com.alex.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class MyProducer {

    /**
     * @description 整体来说，构建Producer分为三个步骤:
     * 1. 设置Producer核心属性 :Producer可选的属性都可以由ProducerConfig类管理。比如 ProducerConfig.BOOTSTRAP_SERVERS_CONFIG属性，显然就是指发送者要将消息发到哪个Kafka集 群上。这是每个Producer必选的属性。在ProducerConfig中，对于大部分比较重要的属性，都配置了 对应的DOC属性进行描述。
     * 2. 构建消息:Kafka的消息是一个Key-Value结构的消息。其中，key和value都可以是任意对象类型。其 中，key主要是用来进行Partition分区的，业务上更关心的是value。
     * 3. 使用Producer发送消息。:通常用到的就是单向发送、同步发送和异步发送者三种发送方式。
     * @return void
     * @param args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
//Part2:构建消息
            ProducerRecord<String, String> record = new ProducerRecord<>("TOPIC",
                    Integer.toString(i), "MyProducer" + i);
//Part3:发送消息
//单向发送:不关心服务端的应答。
            producer.send(record);
            System.out.println("message "+i+" sended"); //同步发送:获取服务端应答消息前，会阻塞当前线程。
            RecordMetadata recordMetadata = producer.send(record).get(); String topic = recordMetadata.topic();
            int partition = recordMetadata.partition();
            long offset = recordMetadata.offset();
            String message = recordMetadata.toString();
            System.out.println("message:["+ message+"] sended with topic:"+topic+"; partition:"+partition+ ";offset:"+offset); //异步发送:消息发送后不阻塞，服务端有应答后会触发回调函数
            producer.send(record, new Callback()
                {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e){
                    if(null != e){ System.out.println("消息发送失败,"+e.getMessage()); e.printStackTrace();
                    }else{
                        String topic = recordMetadata.topic();
                        long offset = recordMetadata.offset();
                        String message = recordMetadata.toString();
                        System.out.println("message:["+ message+"] sended with topic:"+topic+";offset:"+offset);
                    }
                countDownLatch.countDown();
                    }
            });
        }
        try {
            /**
             * <p>CountDownLatch是一个并发工具类，它允许一个或多个线程等待其他线程完成操作。
             * 这个类有一个计数器，初始值可以设定，每调用countDown()方法一次，计数器减1。
             * 其他线程可以通过await()方法阻塞，直到计数器为0，这时所有等待的线程会被释放继续执行。
             * 在这个特定的Java函数中，CountDownLatch countDownLatch = new CountDownLatch(5);
             * 初始化了一个计数器为5的CountDownLatch，意味着主线程将等待5个任务完成。
             * 在for循环内部，每次消息发送后都会执行countDownLatch.countDown();
             * 这会使计数器递减。当所有5条消息都发送完毕（即计数器减到0），
             * countDownLatch.await(); 之后的代码才会被执行，这里主要是producer.close();
             * 用于关闭Kafka生产者连接。这样确保了所有消息都被正确发送出去后，程序才结束，提供了同步机制。</p>
             */
            countDownLatch.await();
        } catch (Exception e) {

        }
        producer.close();
    }
}
