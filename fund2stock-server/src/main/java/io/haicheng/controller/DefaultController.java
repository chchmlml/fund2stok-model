package io.haicheng.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DefaultController {


    @RequestMapping(value = "/test", method = {RequestMethod.GET})
    public Object test() {

        return "everything is ok";
    }

}
