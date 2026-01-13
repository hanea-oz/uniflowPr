package com.uniflow.repository;

import com.uniflow.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findBySpecialty(String specialty);
    
    @Query("SELECT t FROM Teacher t JOIN FETCH t.user u WHERE u.email = :email")
    Optional<Teacher> findByEmail(@Param("email") String email);
    
    @Query("SELECT t FROM Teacher t JOIN FETCH t.user u WHERE LOWER(t.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Teacher> searchTeachers(@Param("query") String query);
}
