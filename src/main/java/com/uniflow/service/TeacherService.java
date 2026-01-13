package com.uniflow.service;

import com.uniflow.model.Teacher;
import com.uniflow.model.User;
import com.uniflow.model.enums.RoleEnum;
import com.uniflow.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserService userService;

    public Teacher createTeacher(String email, String password, String firstName, String lastName,
                                  String grade, String specialty, String skills, Integer workloadHours) {
        // Create user first
        User user = userService.createUser(email, password, RoleEnum.TEACHER);

        // Create teacher
        Teacher teacher = Teacher.builder()
                .user(user)
                .firstName(firstName)
                .lastName(lastName)
                .grade(grade)
                .specialty(specialty)
                .skills(skills)
                .workloadHours(workloadHours != null ? workloadHours : 0)
                .build();

        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, String firstName, String lastName, String grade,
                                  String specialty, String skills, Integer workloadHours) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + id));

        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setGrade(grade);
        teacher.setSpecialty(specialty);
        teacher.setSkills(skills);
        if (workloadHours != null) {
            teacher.setWorkloadHours(workloadHours);
        }

        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Teacher> findByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Teacher> searchTeachers(String query) {
        return teacherRepository.searchTeachers(query);
    }
}
