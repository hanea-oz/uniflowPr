package com.uniflow.repository;

import com.uniflow.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByProgramAndLevel(String program, String level);
    Optional<Group> findByNameAndProgramAndLevel(String name, String program, String level);
    List<Group> findByProgram(String program);
}
