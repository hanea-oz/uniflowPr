package com.uniflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "timeslots", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"day_of_week", "start_time", "end_time"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_of_week", nullable = false, length = 12)
    @NotBlank(message = "Day of week is required")
    @Size(max = 12)
    private String dayOfWeek; // e.g., MONDAY, TUESDAY, etc.

    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Transient
    public String getTimeRange() {
        return startTime + " - " + endTime;
    }
}
