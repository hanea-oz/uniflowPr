package com.uniflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "program", "level"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "Group name is required")
    @Size(max = 80)
    private String name;

    @Column(nullable = false, length = 120)
    @NotBlank(message = "Program is required")
    @Size(max = 120)
    private String program;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Level is required")
    @Size(max = 50)
    private String level;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    @ManyToMany(mappedBy = "assignedGroups", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Module> modules = new HashSet<>();
}
