package com.alex.demo.count;

import com.alex.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Stephen
 * @Date: 2020/3/4 17:42
 * @Content:
 */
@RestController
@CrossOrigin("*")
public class InitCtrl {
    @Autowired
    private ReadService readService;

    @RequestMapping("/data")
    public List<String> findData(int begin,int size){
        return readService.getData(begin,size);
    }

}


