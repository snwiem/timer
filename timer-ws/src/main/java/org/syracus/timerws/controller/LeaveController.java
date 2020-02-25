package org.syracus.timerws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.syracus.timerws.model.LeaveData;
import org.syracus.timerws.service.WorkModelService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping(path = "/leave")
public class LeaveController {

    private static final transient Logger LOG = LoggerFactory.getLogger(LeaveController.class);

    @Autowired
    private WorkModelService workModelService;

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LeaveData leavePost(@RequestBody LeaveData input) {
        LOG.debug("Input = {}", input);
        LocalDateTime arrivalDateTime = LocalDateTime.now().with(input.getArrival().withSecond(0).withNano(0));
        LocalDateTime leaveDateTime = LocalDateTime.now().with(Optional.ofNullable(input.getLeave()).orElse(LocalTime.now()).withSecond(0).withNano(0));
        Optional<Duration> saldo = input.getSaldo();

        LocalDateTime regularLeaveTime = this.workModelService.getRegularLeave(arrivalDateTime);
        LocalDateTime latestLeaveTime = this.workModelService.getLatestLeave(arrivalDateTime);
        LocalDateTime earliestLeaveTime = this.workModelService.getEarliestLeave(arrivalDateTime, saldo);
        LocalDateTime bestLeaveTime = this.workModelService.getBestLeave(arrivalDateTime);

        if (earliestLeaveTime.isBefore(arrivalDateTime))
            earliestLeaveTime = arrivalDateTime;

        Duration newSaldo = this.workModelService.getSaldo(arrivalDateTime, leaveDateTime, saldo);

        Duration attendance = this.workModelService.getAttendance(arrivalDateTime, leaveDateTime);
        Duration workTime = this.workModelService.getWorkTime(attendance);
        Duration pauseTime = this.workModelService.getPauseTimeForAttendance(attendance);

        LeaveData leaveData = LeaveData.builder()
                .arrival(arrivalDateTime.toLocalTime())
                .leave(leaveDateTime.toLocalTime())
                .regular(regularLeaveTime)
                .latest(latestLeaveTime)
                .earliest(earliestLeaveTime)
                .best(bestLeaveTime)
                .saldo(Optional.of(newSaldo))
                .attendance(attendance)
                .work(workTime)
                .pause(pauseTime)
                .build();

        return leaveData;
    }

    @GetMapping(path = {
            "/{arrival}",
            "/{arrival}/{saldo}"
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public LeaveData leave(@PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime arrival, @PathVariable Optional<Duration> saldo) {
        LocalDateTime arrivalDateTime = LocalDateTime.now().with(arrival);
        LeaveData leaveData = LeaveData.builder()
                .earliest(this.workModelService.getEarliestLeave(arrivalDateTime, saldo))
                .latest(this.workModelService.getLatestLeave(arrivalDateTime))
                .regular(this.workModelService.getRegularLeave(arrivalDateTime))
                .best(this.workModelService.getBestLeave(arrivalDateTime))
                .build();
        return leaveData;
        
    }

    @GetMapping(path = {
            "/earliest/{arrival}",
            "/earliest/{arrival}/{saldo}"
    })
    public String earliest(@PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime arrival, @PathVariable Optional<Duration> saldo) {
        LocalDateTime earliestLeave = this.workModelService.getEarliestLeave(
                LocalDateTime
                        .now()
                        .with(arrival),
                saldo);
        return earliestLeave.toString();
    }

    @GetMapping("/latest/{arrival}")
    public String latest(@PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime arrival) {
        LocalDateTime latestLeave = this.workModelService.getLatestLeave(
                LocalDateTime
                        .now()
                        .with(arrival));
        return latestLeave.toString();
    }

    @GetMapping("/best/{arrival}")
    public String best(@PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime arrival) {
        LocalDateTime bestLeave = this.workModelService.getBestLeave(
                LocalDateTime
                        .now()
                        .with(arrival));
        return bestLeave.toString();
    }
}
