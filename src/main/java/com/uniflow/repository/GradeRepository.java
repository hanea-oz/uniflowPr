package com.uniflow.repository;

import com.uniflow.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByEnrollmentId(Long enrollmentId);
    List<Grade> findByGivenByTeacherId(Long teacherId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.module.id = :moduleId")
    List<Grade> findByModuleId(@Param("moduleId") Long moduleId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.module.id = :moduleId AND g.givenByTeacher.id = :teacherId")
    List<Grade> findByModuleIdAndTeacherId(@Param("moduleId") Long moduleId, @Param("teacherId") Long teacherId);
}
