package com.uniflow.service;

import com.uniflow.model.Enrollment;
import com.uniflow.repository.EnrollmentRepository;
import com.uniflow.repository.ModuleRepository;
import com.uniflow.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;

    public Enrollment createEnrollment(Long studentId, Long moduleId) {
        if (enrollmentRepository.existsByStudentIdAndModuleId(studentId, moduleId)) {
            throw new IllegalArgumentException("Student is already enrolled in this module");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(studentRepository.findById(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId)))
                .module(moduleRepository.findById(moduleId)
                        .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId)))
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByModuleId(Long moduleId) {
        return enrollmentRepository.findByModuleId(moduleId);
    }
}
