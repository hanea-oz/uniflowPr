package com.uniflow.service;

import com.uniflow.model.Student;
import com.uniflow.model.User;
import com.uniflow.model.enums.RoleEnum;
import com.uniflow.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserService userService;
    @Lazy
    private final GroupService groupService;

    public Student createStudent(String email, String password, String firstName, String lastName,
                                   String program, String level, String skills, Long groupId) {
        // Create user first
        User user = userService.createUser(email, password, RoleEnum.STUDENT);

        // Create student
        Student student = Student.builder()
                .user(user)
                .firstName(firstName)
                .lastName(lastName)
                .program(program)
                .level(level)
                .skills(skills)
                .group(groupId != null ? groupService.findById(groupId).orElse(null) : null)
                .build();

        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, String firstName, String lastName, String program,
                                   String level, String skills, Long groupId) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setProgram(program);
        student.setLevel(level);
        student.setSkills(skills);
        student.setGroup(groupId != null ? groupService.findById(groupId).orElse(null) : null);

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Student> findByGroupId(Long groupId) {
        return studentRepository.findByGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudents(String query) {
        return studentRepository.searchStudents(query);
    }
}
