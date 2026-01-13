package com.uniflow.repository;

import com.uniflow.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGroupId(Long groupId);
    List<Student> findByProgramAndLevel(String program, String level);
    
    @Query("SELECT s FROM Student s JOIN FETCH s.user u WHERE u.email = :email")
    Optional<Student> findByEmail(@Param("email") String email);
    
    @Query("SELECT s FROM Student s JOIN FETCH s.user u WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);
}
