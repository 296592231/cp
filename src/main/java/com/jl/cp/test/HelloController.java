/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jl
 * @date 2021/2/26 11:36
 */

@Controller
public class HelloController {
    @RequestMapping("/index")
    public String sayHello(){
        return "index";
    }
}
