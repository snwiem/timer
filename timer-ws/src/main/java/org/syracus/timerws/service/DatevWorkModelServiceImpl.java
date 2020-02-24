package org.syracus.timerws.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ValueRange;
import java.util.Optional;

@Service
public class DatevWorkModelServiceImpl implements WorkModelService {

    private static final transient Logger LOG = LoggerFactory.getLogger(DatevWorkModelServiceImpl.class);
    private static final Duration MIN_WORKTIME = Duration.parse("PT8H");
    private static final Duration MAX_WORKTIME = Duration.parse("PT10H");


    @Override
    public LocalDateTime getEarliestLeave(LocalDateTime arrival, Optional<Duration> saldo) {
        return getRegularLeave(arrival).minus(saldo.orElse(Duration.ZERO));
    }

    @Override
    public LocalDateTime getLatestLeave(LocalDateTime arrival) {
        return arrival.plus(getAttendanceForWork(MAX_WORKTIME));
    }

    @Override
    public LocalDateTime getRegularLeave(LocalDateTime arrival) {
        return arrival.plus(getAttendanceForWork(MIN_WORKTIME));
    }

    @Override
    public LocalDateTime getBestLeave(LocalDateTime arrival) {
        return arrival.plus(getBestAttendance(getCurrentAttendance(arrival)));
    }

    @Override
    public Duration getSaldo(LocalDateTime arrival, Optional<Duration> saldo) {
        return getSaldo(arrival, LocalDateTime.now().withSecond(0).withNano(0), saldo);
    }

    @Override
    public Duration getSaldo(LocalDateTime arrival, LocalDateTime leave, Optional<Duration> saldo) {
        Duration workTime = getWorkTime(getAttendance(arrival, leave));
        workTime = workTime.minus(MIN_WORKTIME);
        Duration newSaldo = saldo.orElse(Duration.ZERO).plus(workTime);
        return newSaldo;
    }

    private Duration getCurrentAttendance(LocalDateTime arrival) {
        return getAttendance(arrival, LocalDateTime.now());
    }

    @Override
    public Duration getAttendance(LocalDateTime arrival, LocalDateTime leave) {
        // FIXME: What if arrival > leave ?
        return Duration.between(arrival, leave);
    }

    private Duration getAttendanceForWork(Duration workTime) {
        return workTime.plus(getPauseTimeForWork(workTime));
    }

    private Duration getWorkTime(LocalDateTime arrival) {
        return getWorkTime(arrival, LocalDateTime.now());
    }


    private Duration getPauseTimeForAttendance(LocalDateTime arrival) {
        return getPauseTimeForAttendance(arrival, LocalDateTime.now());
    }

    @Override
    public Duration getWorkTime(LocalDateTime arrival, LocalDateTime leave) {
        return getWorkTime(getAttendance(arrival, leave));
    }

    @Override
    public Duration getPauseTimeForAttendance(LocalDateTime arrival, LocalDateTime leave) {
        return getPauseTimeForAttendance(getAttendance(arrival, leave));
    }

    @Override
    public Duration getWorkTime(Duration attendance) {
        return attendance.minus(getPauseTimeForAttendance(attendance));
    }

    private Duration getBestAttendance(Duration currentAttendance) {
        long attendanceInMinutes = currentAttendance.toMinutes();
        Duration bestSlot = null;
        if (ValueRange.of(Duration.ZERO.toMinutes(), Duration.parse("PT2H").toMinutes()).isValidValue(attendanceInMinutes)) {
            bestSlot = Duration.parse("PT2H");
        } else if (ValueRange.of(Duration.parse("PT2H1M").toMinutes(), Duration.parse("PT2H14M").toMinutes()).isValidValue(attendanceInMinutes)) {
            bestSlot = currentAttendance;
        } else if (ValueRange.of(Duration.parse("PT2H15M").toMinutes(), Duration.parse("PT4H45M").toMinutes()).isValidValue(attendanceInMinutes)) {
            bestSlot = Duration.parse("PT4H45M");
        } else if (ValueRange.of(Duration.parse("PT4H46M").toMinutes(), Duration.parse("PT4H59M").toMinutes()).isValidValue(attendanceInMinutes)) {
            bestSlot = currentAttendance;
        } else if (ValueRange.of(Duration.parse("PT5H").toMinutes(), Duration.parse("PT6H30M").toMinutes()).isValidValue(attendanceInMinutes)) {
            bestSlot = Duration.parse("PT6H30M");
        } else if (ValueRange.of(Duration.parse("PT6H31M").toMinutes(), Duration.parse("PT6H44M").toMinutes()).isValidValue(attendanceInMinutes)) {
            bestSlot = currentAttendance;
        } else if (ValueRange.of(Duration.parse("PT6H45M").toMinutes(), MIN_WORKTIME.plus(getPauseTimeForWork(MIN_WORKTIME)).toMinutes()).isValidValue(attendanceInMinutes)) {
            bestSlot = MIN_WORKTIME.plus(getPauseTimeForWork(MIN_WORKTIME));
        } else if (ValueRange.of(Duration.parse("PT6H45M").toMinutes(), Long.MAX_VALUE).isValidValue(attendanceInMinutes)) {
            bestSlot = currentAttendance;
        }
        return bestSlot;
    }

    @Override
    public Duration getPauseTimeForAttendance(Duration attendance) {
        long attendanceInMinutes = attendance.toMinutes();
        long pauseInMinutes = 0;
        if (ValueRange.of(Duration.ZERO.toMinutes(), Duration.parse("PT2H").toMinutes()).isValidValue(attendanceInMinutes)) {
            pauseInMinutes = 0;
        } else if (ValueRange.of(Duration.parse("PT2H1M").toMinutes(), Duration.parse("PT2H14M").toMinutes()).isValidValue(attendanceInMinutes)) {
            pauseInMinutes = 0 + attendanceInMinutes - Duration.parse("PT2H1M").toMinutes();
        } else if (ValueRange.of(Duration.parse("PT2H15M").toMinutes(), Duration.parse("PT4H45M").toMinutes()).isValidValue(attendanceInMinutes)) {
            pauseInMinutes = 15;
        } else if (ValueRange.of(Duration.parse("PT4H46M").toMinutes(), Duration.parse("PT4H59M").toMinutes()).isValidValue(attendanceInMinutes)) {
            pauseInMinutes = 15 + attendanceInMinutes - Duration.parse("PT4H46M").toMinutes();
        } else if (ValueRange.of(Duration.parse("PT5H").toMinutes(), Duration.parse("PT6H30M").toMinutes()).isValidValue(attendanceInMinutes)) {
            pauseInMinutes = 30;
        } else if (ValueRange.of(Duration.parse("PT6H31M").toMinutes(), Duration.parse("PT6H44M").toMinutes()).isValidValue(attendanceInMinutes)) {
            pauseInMinutes = 40 + attendanceInMinutes - Duration.parse("PT6H31M").toMinutes();
        } else if (ValueRange.of(Duration.parse("PT6H45M").toMinutes(), Long.MAX_VALUE).isValidValue(attendanceInMinutes)) {
            pauseInMinutes = 45;
        }
        return Duration.ofMinutes(pauseInMinutes);
    }

    private Duration getPauseTimeForWork(Duration work) {
        long workInMinutes = work.toMinutes();
        long pauseInMinutes = 0;
        if (ValueRange.of(Duration.ZERO.toMinutes(), Duration.parse("PT2H").toMinutes()).isValidValue(workInMinutes)) {
            pauseInMinutes = 0;
        } else if (ValueRange.of(Duration.parse("PT2H1M").toMinutes(), Duration.parse("PT4H30M").toMinutes()).isValidValue(workInMinutes)) {
            pauseInMinutes = 15;
        } else if (ValueRange.of(Duration.parse("PT4H31M").toMinutes(), Duration.parse("PT6H").toMinutes()).isValidValue(workInMinutes)) {
            pauseInMinutes = 30;
        } else if (ValueRange.of(Duration.parse("PT6H1M").toMinutes(), Long.MAX_VALUE).isValidValue(workInMinutes)) {
            pauseInMinutes = 45;
        }
        return Duration.ofMinutes(pauseInMinutes);
    }

}
