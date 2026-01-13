package com.uniflow.service;

import com.uniflow.model.Session;
import com.uniflow.model.enums.SessionTypeEnum;
import com.uniflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ModuleRepository moduleRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final TimeslotRepository timeslotRepository;
    private final ConflictDetectionService conflictDetectionService;

    public Session createSession(SessionTypeEnum type, Long moduleId, Long teacherId,
                                  Long groupId, Long roomId, Long timeslotId) {
        // Validation préalable des conflits
        validateConflictsBeforeCreation(teacherId, groupId, roomId, timeslotId);
        
        try {
            Session session = Session.builder()
                    .type(type)
                    .module(moduleRepository.findById(moduleId)
                            .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId)))
                    .teacher(teacherRepository.findById(teacherId)
                            .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId)))
                    .group(groupRepository.findById(groupId)
                            .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId)))
                    .room(roomRepository.findById(roomId)
                            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId)))
                    .timeslot(timeslotRepository.findById(timeslotId)
                            .orElseThrow(() -> new IllegalArgumentException("Timeslot not found: " + timeslotId)))
                    .build();

            return sessionRepository.save(session);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message.contains("uq_session_room_timeslot")) {
                throw new IllegalArgumentException("Conflict: Room is already booked for this timeslot");
            } else if (message.contains("uq_session_teacher_timeslot")) {
                throw new IllegalArgumentException("Conflict: Teacher is already scheduled for this timeslot");
            } else if (message.contains("uq_session_group_timeslot")) {
                throw new IllegalArgumentException("Conflict: Group is already scheduled for this timeslot");
            }
            throw new IllegalArgumentException("Timetable conflict occurred: " + e.getMessage());
        }
    }

    public Session updateSession(Long id, SessionTypeEnum type, Long moduleId, Long teacherId,
                                  Long groupId, Long roomId, Long timeslotId) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + id));

        try {
            session.setType(type);
            session.setModule(moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId)));
            session.setTeacher(teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId)));
            session.setGroup(groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId)));
            session.setRoom(roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId)));
            session.setTimeslot(timeslotRepository.findById(timeslotId)
                    .orElseThrow(() -> new IllegalArgumentException("Timeslot not found: " + timeslotId)));

            return sessionRepository.save(session);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message.contains("uq_session_room_timeslot")) {
                throw new IllegalArgumentException("Conflict: Room is already booked for this timeslot");
            } else if (message.contains("uq_session_teacher_timeslot")) {
                throw new IllegalArgumentException("Conflict: Teacher is already scheduled for this timeslot");
            } else if (message.contains("uq_session_group_timeslot")) {
                throw new IllegalArgumentException("Conflict: Group is already scheduled for this timeslot");
            }
            throw new IllegalArgumentException("Timetable conflict occurred: " + e.getMessage());
        }
    }

    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Session> findByIdWithRelations(Long id) {
        return sessionRepository.findByIdWithRelations(id);
    }

    @Transactional(readOnly = true)
    public List<Session> findAll() {
        return sessionRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public List<Session> findByGroupId(Long groupId) {
        return sessionRepository.findByGroupIdOrderByTimeslot(groupId);
    }

    @Transactional(readOnly = true)
    public List<Session> findByTeacherId(Long teacherId) {
        return sessionRepository.findByTeacherIdOrderByTimeslot(teacherId);
    }

    // ========================================
    // GESTION AVANCÉE DES CONFLITS
    // ========================================

    private void validateConflictsBeforeCreation(Long teacherId, Long groupId, Long roomId, Long timeslotId) {
        validateTeacherAvailability(teacherId, timeslotId);
        validateGroupAvailability(groupId, timeslotId);
        validateRoomAvailability(roomId, timeslotId);
        validateRoomCapacity(roomId, groupId);
        
        // Validation avancée des conflits d'étudiants
        conflictDetectionService.validateStudentScheduleConflicts(groupId, timeslotId);
    }

    private void validateTeacherAvailability(Long teacherId, Long timeslotId) {
        List<Session> teacherSessions = sessionRepository.findByTeacherIdAndTimeslotId(teacherId, timeslotId);
        if (!teacherSessions.isEmpty()) {
            String teacherName = teacherRepository.findById(teacherId)
                .map(t -> t.getFirstName() + " " + t.getLastName())
                .orElse("Enseignant ID: " + teacherId);
            throw new IllegalArgumentException(
                "Conflit détecté : L'enseignant " + teacherName + " a déjà un cours programmé à ce créneau.");
        }
    }

    private void validateGroupAvailability(Long groupId, Long timeslotId) {
        List<Session> groupSessions = sessionRepository.findByGroupIdAndTimeslotId(groupId, timeslotId);
        if (!groupSessions.isEmpty()) {
            String groupName = groupRepository.findById(groupId)
                .map(g -> g.getName())
                .orElse("Groupe ID: " + groupId);
            throw new IllegalArgumentException(
                "Conflit détecté : Le groupe " + groupName + " a déjà un cours programmé à ce créneau.");
        }
    }

    private void validateRoomAvailability(Long roomId, Long timeslotId) {
        List<Session> roomSessions = sessionRepository.findByRoomIdAndTimeslotId(roomId, timeslotId);
        if (!roomSessions.isEmpty()) {
            String roomName = roomRepository.findById(roomId)
                .map(r -> r.getName())
                .orElse("Salle ID: " + roomId);
            throw new IllegalArgumentException(
                "Conflit détecté : La salle " + roomName + " est déjà occupée à ce créneau.");
        }
    }

    private void validateRoomCapacity(Long roomId, Long groupId) {
        var room = roomRepository.findById(roomId).orElse(null);
        var group = groupRepository.findById(groupId).orElse(null);
        
        if (room == null || group == null) {
            return;
        }

        // Compter les étudiants dans le groupe
        long studentCount = group.getStudents().size();
        
        if (studentCount > room.getCapacity()) {
            throw new IllegalArgumentException(
                String.format("Conflit de capacité : La salle %s (capacité: %d) ne peut accueillir le groupe %s (%d étudiants).",
                    room.getName(), room.getCapacity(), group.getName(), studentCount));
        }
    }

    /**
     * Méthode utilitaire pour vérifier les conflits avant mise à jour
     */
    public void validateSessionUpdate(Long sessionId, Long teacherId, Long groupId, Long roomId, Long timeslotId) {
        // Exclure la session actuelle des vérifications de conflit
        validateTeacherAvailabilityExcluding(teacherId, timeslotId, sessionId);
        validateGroupAvailabilityExcluding(groupId, timeslotId, sessionId);
        validateRoomAvailabilityExcluding(roomId, timeslotId, sessionId);
        validateRoomCapacity(roomId, groupId);
    }

    private void validateTeacherAvailabilityExcluding(Long teacherId, Long timeslotId, Long excludeSessionId) {
        List<Session> teacherSessions = sessionRepository.findByTeacherIdAndTimeslotId(teacherId, timeslotId)
            .stream()
            .filter(s -> !s.getId().equals(excludeSessionId))
            .toList();
        if (!teacherSessions.isEmpty()) {
            String teacherName = teacherRepository.findById(teacherId)
                .map(t -> t.getFirstName() + " " + t.getLastName())
                .orElse("Enseignant ID: " + teacherId);
            throw new IllegalArgumentException(
                "Conflit détecté : L'enseignant " + teacherName + " a déjà un cours programmé à ce créneau.");
        }
    }

    private void validateGroupAvailabilityExcluding(Long groupId, Long timeslotId, Long excludeSessionId) {
        List<Session> groupSessions = sessionRepository.findByGroupIdAndTimeslotId(groupId, timeslotId)
            .stream()
            .filter(s -> !s.getId().equals(excludeSessionId))
            .toList();
        if (!groupSessions.isEmpty()) {
            String groupName = groupRepository.findById(groupId)
                .map(g -> g.getName())
                .orElse("Groupe ID: " + groupId);
            throw new IllegalArgumentException(
                "Conflit détecté : Le groupe " + groupName + " a déjà un cours programmé à ce créneau.");
        }
    }

    private void validateRoomAvailabilityExcluding(Long roomId, Long timeslotId, Long excludeSessionId) {
        List<Session> roomSessions = sessionRepository.findByRoomIdAndTimeslotId(roomId, timeslotId)
            .stream()
            .filter(s -> !s.getId().equals(excludeSessionId))
            .toList();
        if (!roomSessions.isEmpty()) {
            String roomName = roomRepository.findById(roomId)
                .map(r -> r.getName())
                .orElse("Salle ID: " + roomId);
            throw new IllegalArgumentException(
                "Conflit détecté : La salle " + roomName + " est déjà occupée à ce créneau.");
        }
    }
}
