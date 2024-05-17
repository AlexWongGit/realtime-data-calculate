package com.alex.demo.count;

import com.alex.demo.count.Agg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @Author: Stephen
 * @Date: 2020/3/4 11:47
 * @Content:
 */
public class AggDeseriallizer implements Deserializer<Agg> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public Agg deserialize(String s, byte[] bytes) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(bytes,Agg.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void close() {

    }
}

