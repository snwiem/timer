package org.syracus.timerws.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Getter
@Setter
@Builder
public class LeaveData {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arrival;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime leave;
    private Optional<Duration> saldo = Optional.empty();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime earliest;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime latest;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime regular;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime best;

    private Duration attendance;
    private Duration pause;
    private Duration work;
    private boolean waiting;

}
