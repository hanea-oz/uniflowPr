package com.uniflow.model;

import com.uniflow.model.enums.SessionTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "sessions", uniqueConstraints = {
    @UniqueConstraint(name = "uq_session_room_timeslot", columnNames = {"room_id", "timeslot_id"}),
    @UniqueConstraint(name = "uq_session_teacher_timeslot", columnNames = {"teacher_id", "timeslot_id"}),
    @UniqueConstraint(name = "uq_session_group_timeslot", columnNames = {"group_id", "timeslot_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "session_type_enum")
    @NotNull(message = "Session type is required")
    private SessionTypeEnum type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    @NotNull(message = "Module is required")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    @NotNull(message = "Teacher is required")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull(message = "Group is required")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    @NotNull(message = "Room is required")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timeslot_id", nullable = false)
    @NotNull(message = "Timeslot is required")
    private Timeslot timeslot;
}
