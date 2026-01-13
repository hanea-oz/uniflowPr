package com.uniflow.repository;

import com.uniflow.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByModuleId(Long moduleId);
    Optional<Enrollment> findByStudentIdAndModuleId(Long studentId, Long moduleId);
    
    @Query("SELECT e FROM Enrollment e JOIN e.module m WHERE m.responsibleTeacher.id = :teacherId")
    List<Enrollment> findByTeacherId(@Param("teacherId") Long teacherId);
    
    boolean existsByStudentIdAndModuleId(Long studentId, Long moduleId);
}
