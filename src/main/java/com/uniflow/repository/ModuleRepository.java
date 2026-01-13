package com.uniflow.repository;

import com.uniflow.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByProgramAndSemester(String program, Integer semester);
    Optional<Module> findByNameAndProgramAndSemester(String name, String program, Integer semester);
    List<Module> findByResponsibleTeacherId(Long teacherId);
    
    @Query("SELECT m FROM Module m JOIN m.assignedGroups g WHERE g.id = :groupId")
    List<Module> findByGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT DISTINCT m FROM Module m JOIN Enrollment e ON e.module.id = m.id WHERE e.student.id = :studentId")
    List<Module> findByStudentId(@Param("studentId") Long studentId);
}
