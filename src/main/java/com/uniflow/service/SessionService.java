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

    public Session createSession(SessionTypeEnum type, Long moduleId, Long teacherId,
                                  Long groupId, Long roomId, Long timeslotId) {
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
}
