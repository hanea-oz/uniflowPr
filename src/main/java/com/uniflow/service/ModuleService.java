package com.uniflow.service;

import com.uniflow.model.Module;
import com.uniflow.repository.GroupRepository;
import com.uniflow.repository.ModuleRepository;
import com.uniflow.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;

    public Module createModule(String name, String program, Integer semester, Integer volumeHours,
                                String requiredSkills, Long responsibleTeacherId) {
        if (moduleRepository.findByNameAndProgramAndSemester(name, program, semester).isPresent()) {
            throw new IllegalArgumentException("Module already exists: " + name + " (" + program + ", S" + semester + ")");
        }

        Module module = Module.builder()
                .name(name)
                .program(program)
                .semester(semester)
                .volumeHours(volumeHours)
                .requiredSkills(requiredSkills)
                .responsibleTeacher(responsibleTeacherId != null ?
                        teacherRepository.findById(responsibleTeacherId).orElse(null) : null)
                .build();

        return moduleRepository.save(module);
    }

    public Module updateModule(Long id, String name, String program, Integer semester, Integer volumeHours,
                                String requiredSkills, Long responsibleTeacherId) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + id));

        Optional<Module> existing = moduleRepository.findByNameAndProgramAndSemester(name, program, semester);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Module already exists: " + name + " (" + program + ", S" + semester + ")");
        }

        module.setName(name);
        module.setProgram(program);
        module.setSemester(semester);
        module.setVolumeHours(volumeHours);
        module.setRequiredSkills(requiredSkills);
        module.setResponsibleTeacher(responsibleTeacherId != null ?
                teacherRepository.findById(responsibleTeacherId).orElse(null) : null);

        return moduleRepository.save(module);
    }

    public void assignGroupsToModule(Long moduleId, Set<Long> groupIds) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        Set<com.uniflow.model.Group> groups = groupIds.stream()
                .map(groupId -> groupRepository.findById(groupId)
                        .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId)))
                .collect(Collectors.toSet());

        module.setAssignedGroups(groups);
        moduleRepository.save(module);
    }

    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Module> findById(Long id) {
        return moduleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Module> findAll() {
        return moduleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Module> findByTeacherId(Long teacherId) {
        return moduleRepository.findByResponsibleTeacherId(teacherId);
    }

    @Transactional(readOnly = true)
    public List<Module> findByGroupId(Long groupId) {
        return moduleRepository.findByGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public List<Module> findByStudentId(Long studentId) {
        return moduleRepository.findByStudentId(studentId);
    }
}
