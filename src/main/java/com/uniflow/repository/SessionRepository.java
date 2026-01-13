package com.uniflow.repository;

import com.uniflow.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByGroupId(Long groupId);
    List<Session> findByTeacherId(Long teacherId);
    List<Session> findByModuleId(Long moduleId);
    List<Session> findByRoomId(Long roomId);
    List<Session> findByTimeslotId(Long timeslotId);
    
    // Méthodes pour la détection de conflits
    List<Session> findByTeacherIdAndTimeslotId(Long teacherId, Long timeslotId);
    List<Session> findByGroupIdAndTimeslotId(Long groupId, Long timeslotId);
    List<Session> findByRoomIdAndTimeslotId(Long roomId, Long timeslotId);
    
    @Query("SELECT s FROM Session s WHERE s.group.id = :groupId ORDER BY s.timeslot.dayOfWeek, s.timeslot.startTime")
    List<Session> findByGroupIdOrderByTimeslot(@Param("groupId") Long groupId);
    
    @Query("SELECT s FROM Session s WHERE s.teacher.id = :teacherId ORDER BY s.timeslot.dayOfWeek, s.timeslot.startTime")
    List<Session> findByTeacherIdOrderByTimeslot(@Param("teacherId") Long teacherId);
    
    @Query("SELECT DISTINCT s FROM Session s " +
           "LEFT JOIN FETCH s.module " +
           "LEFT JOIN FETCH s.teacher " +
           "LEFT JOIN FETCH s.group " +
           "LEFT JOIN FETCH s.room " +
           "LEFT JOIN FETCH s.timeslot " +
           "WHERE s.id = :id")
    Optional<Session> findByIdWithRelations(@Param("id") Long id);
    
    @Query("SELECT DISTINCT s FROM Session s " +
           "LEFT JOIN FETCH s.module " +
           "LEFT JOIN FETCH s.teacher " +
           "LEFT JOIN FETCH s.group " +
           "LEFT JOIN FETCH s.room " +
           "LEFT JOIN FETCH s.timeslot")
    List<Session> findAllWithRelations();
}
