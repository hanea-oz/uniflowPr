package com.uniflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "modules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "program", "semester"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "Module name is required")
    @Size(max = 150)
    private String name;

    @Column(nullable = false, length = 120)
    @NotBlank(message = "Program is required")
    @Size(max = 120)
    private String program;

    @Column(nullable = false)
    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be positive")
    private Integer semester;

    @Column(name = "volume_hours", nullable = false)
    @NotNull(message = "Volume hours is required")
    @Min(value = 1, message = "Volume hours must be positive")
    private Integer volumeHours;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_teacher_id")
    private Teacher responsibleTeacher;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "module_groups",
        joinColumns = @JoinColumn(name = "module_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Builder.Default
    private Set<Group> assignedGroups = new HashSet<>();
}
