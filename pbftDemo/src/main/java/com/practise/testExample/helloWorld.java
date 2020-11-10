package com.practise.testExample;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("hello")
public class helloWorld {

    @GetMapping
    public Map testGet() {

        return new HashMap<String, String>() {{
            put("name", "springboot");
        }};
    }

    @GetMapping(path = "str")
    public String testGetStr() {
        return "OK";
    }


}
