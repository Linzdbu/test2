package com.duansq.demo.practise.readExcel;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class demoController {
    @GetMapping("/toHtml")
    String test(HttpServletRequest request) {

        return "excelImport";
    }


}
