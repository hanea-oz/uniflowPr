package com.uniflow.service;

import com.uniflow.model.Grade;
import com.uniflow.repository.EnrollmentRepository;
import com.uniflow.repository.GradeRepository;
import com.uniflow.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeService {

    private final GradeRepository gradeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TeacherRepository teacherRepository;

    public Grade createGrade(Long enrollmentId, Long teacherId, Double labGrade, Double examGrade,
                             Double projectGrade, Double participationGrade, Double finalGrade, String feedback) {
        if (gradeRepository.findByEnrollmentId(enrollmentId).isPresent()) {
            throw new IllegalArgumentException("Grade already exists for this enrollment");
        }

        Grade grade = Grade.builder()
                .enrollment(enrollmentRepository.findById(enrollmentId)
                        .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId)))
                .givenByTeacher(teacherRepository.findById(teacherId)
                        .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId)))
                .labGrade(labGrade)
                .examGrade(examGrade)
                .projectGrade(projectGrade)
                .participationGrade(participationGrade)
                .finalGrade(finalGrade)
                .feedback(feedback)
                .build();

        return gradeRepository.save(grade);
    }

    public Grade updateGrade(Long id, Double labGrade, Double examGrade, Double projectGrade,
                             Double participationGrade, Double finalGrade, String feedback) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found: " + id));

        grade.setLabGrade(labGrade);
        grade.setExamGrade(examGrade);
        grade.setProjectGrade(projectGrade);
        grade.setParticipationGrade(participationGrade);
        grade.setFinalGrade(finalGrade);
        grade.setFeedback(feedback);

        return gradeRepository.save(grade);
    }

    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Grade> findByEnrollmentId(Long enrollmentId) {
        return gradeRepository.findByEnrollmentId(enrollmentId);
    }

    @Transactional(readOnly = true)
    public List<Grade> findByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Grade> findByModuleId(Long moduleId) {
        return gradeRepository.findByModuleId(moduleId);
    }

    @Transactional(readOnly = true)
    public List<Grade> findByModuleIdAndTeacherId(Long moduleId, Long teacherId) {
        return gradeRepository.findByModuleIdAndTeacherId(moduleId, teacherId);
    }
}
