package com.alex.demo.count;

import com.alex.demo.count.Agg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * @Author: Stephen
 * @Date: 2020/3/4 11:43
 * @Content:
 */
public class AggSeriallizer implements Serializer<Agg> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, Agg agg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsBytes(agg);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public void close() {

    }
}


