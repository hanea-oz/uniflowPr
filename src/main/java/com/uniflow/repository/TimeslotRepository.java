package com.uniflow.repository;

import com.uniflow.model.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
    List<Timeslot> findByDayOfWeekOrderByStartTime(String dayOfWeek);
    Optional<Timeslot> findByDayOfWeekAndStartTimeAndEndTime(String dayOfWeek, LocalTime startTime, LocalTime endTime);
    List<Timeslot> findAllByOrderByDayOfWeekAscStartTimeAsc();
}
