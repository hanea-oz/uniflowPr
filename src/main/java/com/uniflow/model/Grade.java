package com.uniflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    @NotNull(message = "Enrollment is required")
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "given_by_teacher_id", nullable = false)
    @NotNull(message = "Teacher is required")
    private Teacher givenByTeacher;

    @Column(name = "lab_grade")
    @DecimalMin(value = "0.0", message = "Lab grade must be between 0 and 20")
    @DecimalMax(value = "20.0", message = "Lab grade must be between 0 and 20")
    private Double labGrade;

    @Column(name = "exam_grade")
    @DecimalMin(value = "0.0", message = "Exam grade must be between 0 and 20")
    @DecimalMax(value = "20.0", message = "Exam grade must be between 0 and 20")
    private Double examGrade;

    @Column(name = "project_grade")
    @DecimalMin(value = "0.0", message = "Project grade must be between 0 and 20")
    @DecimalMax(value = "20.0", message = "Project grade must be between 0 and 20")
    private Double projectGrade;

    @Column(name = "participation_grade")
    @DecimalMin(value = "0.0", message = "Participation grade must be between 0 and 20")
    @DecimalMax(value = "20.0", message = "Participation grade must be between 0 and 20")
    private Double participationGrade;

    @Column(name = "final_grade")
    @DecimalMin(value = "0.0", message = "Final grade must be between 0 and 20")
    @DecimalMax(value = "20.0", message = "Final grade must be between 0 and 20")
    private Double finalGrade;

    @Column(columnDefinition = "TEXT")
    private String feedback;
}
