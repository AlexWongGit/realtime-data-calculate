package com.alex.service.impl;

import com.alex.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadServiceImpl implements ReadService {

    @Autowired
    private StringRedisTemplate template;

    @Override
    public List<String> getData(int begin, int count) {
        // 首先计算开始位置和结束位置
        int over = begin + count - 1;
        // 获取需要的数据
        return template.opsForList().range("gp2", begin, over);
    }
}
