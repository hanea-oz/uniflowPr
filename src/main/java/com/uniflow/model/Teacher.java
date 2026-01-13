package com.uniflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    private Long id; // Same as User ID (PK = FK)

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @Column(length = 80)
    @Size(max = 80)
    private String grade;

    @Column(length = 120)
    @Size(max = 120)
    private String specialty;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "workload_hours", nullable = false)
    @Min(value = 0, message = "Workload hours must be non-negative")
    private Integer workloadHours = 0;
}
