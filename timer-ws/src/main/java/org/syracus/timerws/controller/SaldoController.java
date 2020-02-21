package org.syracus.timerws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.syracus.timerws.model.LeaveData;
import org.syracus.timerws.model.SaldoData;
import org.syracus.timerws.service.WorkModelService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping(path = "/saldo")
public class SaldoController {

    private static final transient Logger LOG = LoggerFactory.getLogger(SaldoController.class);

    @Autowired
    private WorkModelService workModelService;

    @GetMapping(path = {
            "/now/{arrival}",
            "/now/{arrival}/{saldo}"
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Duration saldo(@PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime arrival, @PathVariable Optional<Duration> saldo) {
        Duration newSaldo = this.workModelService.getSaldo(LocalDateTime.now().with(arrival), saldo);
        return newSaldo;
    }

    @GetMapping(path = {
            "/plan/{arrival}/{leave}",
            "/plan/{arrival}/{leave}/{saldo}"
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Duration saldo(@PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime arrival, @PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime leave, @PathVariable Optional<Duration> saldo) {
        Duration newSaldo = this.workModelService.getSaldo(LocalDateTime.now().with(arrival), LocalDateTime.now().with(leave), saldo);
        return newSaldo;
    }

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SaldoData saldoData(@RequestBody SaldoData input) {
        LocalDateTime arrivalDateTime = LocalDateTime.now().with(input.getArrival());
        LocalDateTime leaveDateTime = LocalDateTime.now().with(input.getLeave().orElse(LocalTime.now().withSecond(0).withNano(0)));
        Duration newSaldo = this.workModelService.getSaldo(arrivalDateTime, leaveDateTime, input.getSaldo());
        SaldoData newSaldoData = SaldoData.builder()
                .arrival(arrivalDateTime.toLocalTime())
                .leave(Optional.of(leaveDateTime.toLocalTime()))
                .saldo(Optional.of(newSaldo))
                .build();
        return newSaldoData;
    }
}
