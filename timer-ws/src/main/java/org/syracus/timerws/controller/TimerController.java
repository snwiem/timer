package org.syracus.timerws.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/timer")
public class TimerController {

    @GetMapping(path = "/ping")
    public String ping() {
        return "pong";
    }

}
