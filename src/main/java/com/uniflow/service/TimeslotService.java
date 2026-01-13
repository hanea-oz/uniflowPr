package com.uniflow.service;

import com.uniflow.model.Timeslot;
import com.uniflow.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;

    public Timeslot createTimeslot(String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (timeslotRepository.findByDayOfWeekAndStartTimeAndEndTime(dayOfWeek, startTime, endTime).isPresent()) {
            throw new IllegalArgumentException("Timeslot already exists");
        }

        Timeslot timeslot = Timeslot.builder()
                .dayOfWeek(dayOfWeek.toUpperCase())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        return timeslotRepository.save(timeslot);
    }

    public Timeslot updateTimeslot(Long id, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Timeslot timeslot = timeslotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timeslot not found: " + id));

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        timeslot.setDayOfWeek(dayOfWeek.toUpperCase());
        timeslot.setStartTime(startTime);
        timeslot.setEndTime(endTime);

        return timeslotRepository.save(timeslot);
    }

    public void deleteTimeslot(Long id) {
        timeslotRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Timeslot> findById(Long id) {
        return timeslotRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Timeslot> findAll() {
        return timeslotRepository.findAllByOrderByDayOfWeekAscStartTimeAsc();
    }

    @Transactional(readOnly = true)
    public List<Timeslot> findByDayOfWeek(String dayOfWeek) {
        return timeslotRepository.findByDayOfWeekOrderByStartTime(dayOfWeek.toUpperCase());
    }
}
