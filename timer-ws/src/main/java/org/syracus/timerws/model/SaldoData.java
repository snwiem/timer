package org.syracus.timerws.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

@Getter
@Setter
@Builder
public class SaldoData {

    private LocalTime arrival = null;
    private Optional<LocalTime> leave = Optional.empty();
    private Optional<Duration> saldo = Optional.empty();

}
