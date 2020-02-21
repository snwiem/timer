package org.syracus.timerws.service;

import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public interface WorkModelService {

    LocalDateTime getEarliestLeave(LocalDateTime arrival, Optional<Duration> saldo);
    LocalDateTime getLatestLeave(LocalDateTime arrival);
    LocalDateTime getRegularLeave(LocalDateTime arrival);
    LocalDateTime getBestLeave(LocalDateTime arrival);

    Duration getSaldo(LocalDateTime arrival, Optional<Duration> saldo);
    Duration getSaldo(LocalDateTime arrival, LocalDateTime leave, Optional<Duration> saldo);

    Duration getWorkTime(LocalDateTime arrival, LocalDateTime leave);
    Duration getPauseTimeForAttendance(LocalDateTime arrival, LocalDateTime leave);
    Duration getWorkTime(Duration attendance);
    Duration getAttendance(LocalDateTime arrival, LocalDateTime leave);
    Duration getPauseTimeForAttendance(Duration attendance);

}
