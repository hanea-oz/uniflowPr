package com.uniflow.service;

import com.uniflow.model.Group;
import com.uniflow.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;

    public Group createGroup(String name, String program, String level) {
        if (groupRepository.findByNameAndProgramAndLevel(name, program, level).isPresent()) {
            throw new IllegalArgumentException("Group already exists: " + name + " (" + program + ", " + level + ")");
        }

        Group group = Group.builder()
                .name(name)
                .program(program)
                .level(level)
                .build();

        return groupRepository.save(group);
    }

    public Group updateGroup(Long id, String name, String program, String level) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + id));

        Optional<Group> existing = groupRepository.findByNameAndProgramAndLevel(name, program, level);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Group already exists: " + name + " (" + program + ", " + level + ")");
        }

        group.setName(name);
        group.setProgram(program);
        group.setLevel(level);

        return groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Group> findById(Long id) {
        return groupRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Group> findByProgram(String program) {
        return groupRepository.findByProgram(program);
    }
}
