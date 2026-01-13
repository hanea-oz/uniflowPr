package com.uniflow.service;

import com.uniflow.model.Enrollment;
import com.uniflow.model.Session;
import com.uniflow.model.Student;
import com.uniflow.repository.EnrollmentRepository;
import com.uniflow.repository.SessionRepository;
import com.uniflow.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service utilitaire pour la détection avancée de conflits de planification
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConflictDetectionService {

    private final SessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    /**
     * Vérifie si les étudiants d'un groupe ont des conflits de créneaux
     */
    public void validateStudentScheduleConflicts(Long groupId, Long timeslotId) {
        // Récupérer toutes les sessions existantes pour ce créneau
        List<Session> existingSessions = sessionRepository.findByTimeslotId(timeslotId);
        
        if (existingSessions.isEmpty()) {
            return;
        }

        // Récupérer les étudiants du groupe cible
        List<Student> groupStudents = studentRepository.findByGroupId(groupId);
        
        for (Session existingSession : existingSessions) {
            if (existingSession.getGroup().getId().equals(groupId)) {
                // Même groupe - pas de conflit
                continue;
            }

            // Vérifier si des étudiants du groupe sont aussi dans le module de la session existante
            List<Long> conflictingStudentIds = findConflictingStudents(groupStudents, existingSession.getModule().getId());
            
            if (!conflictingStudentIds.isEmpty()) {
                String conflictingStudentNames = getStudentNames(conflictingStudentIds);
                throw new IllegalArgumentException(
                    String.format("Conflit de planification détecté : Les étudiants suivants ont déjà un cours à ce créneau : %s", 
                        conflictingStudentNames));
            }
        }
    }

    /**
     * Trouve les étudiants d'un groupe qui sont inscrits à un module donné
     */
    private List<Long> findConflictingStudents(List<Student> groupStudents, Long moduleId) {
        Set<Long> groupStudentIds = groupStudents.stream()
            .map(Student::getId)
            .collect(Collectors.toSet());

        return enrollmentRepository.findByModuleId(moduleId).stream()
            .map(enrollment -> enrollment.getStudent().getId())
            .filter(groupStudentIds::contains)
            .toList();
    }

    /**
     * Récupère les noms complets des étudiants
     */
    private String getStudentNames(List<Long> studentIds) {
        return studentRepository.findAllById(studentIds).stream()
            .map(student -> student.getFirstName() + " " + student.getLastName())
            .collect(Collectors.joining(", "));
    }

    /**
     * Vérifie si un étudiant a un conflit de créneau lors de l'inscription à un nouveau module
     */
    public void validateStudentEnrollmentConflicts(Long studentId, Long moduleId) {
        // Récupérer toutes les sessions du module cible
        List<Session> moduleSessions = sessionRepository.findByModuleId(moduleId);
        
        // Récupérer toutes les inscriptions actuelles de l'étudiant
        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(studentId);
        
        for (Session moduleSession : moduleSessions) {
            for (Enrollment enrollment : studentEnrollments) {
                // Récupérer les sessions du module où l'étudiant est déjà inscrit
                List<Session> enrolledModuleSessions = sessionRepository.findByModuleId(enrollment.getModule().getId());
                
                for (Session enrolledSession : enrolledModuleSessions) {
                    if (enrolledSession.getTimeslot().getId().equals(moduleSession.getTimeslot().getId())) {
                        String studentName = studentRepository.findById(studentId)
                            .map(s -> s.getFirstName() + " " + s.getLastName())
                            .orElse("Étudiant ID: " + studentId);
                        
                        throw new IllegalArgumentException(
                            String.format("Conflit de planning : L'étudiant %s a déjà un cours de %s à ce créneau (%s).",
                                studentName, 
                                enrollment.getModule().getName(),
                                enrolledSession.getTimeslot().getDayOfWeek() + " " + enrolledSession.getTimeslot().getStartTime()));
                    }
                }
            }
        }
    }

    /**
     * Génère un rapport des conflits potentiels dans tout le système
     */
    public ConflictReport generateConflictReport() {
        ConflictReport report = new ConflictReport();
        
        // Analyser tous les créneaux
        List<Session> allSessions = sessionRepository.findAll();
        
        // Grouper par timeslot pour détecter les conflits
        var sessionsByTimeslot = allSessions.stream()
            .collect(Collectors.groupingBy(session -> session.getTimeslot().getId()));
        
        sessionsByTimeslot.forEach((timeslotId, sessions) -> {
            if (sessions.size() > 1) {
                // Analyser les conflits dans ce créneau
                analyzeTimeslotConflicts(sessions, report);
            }
        });
        
        return report;
    }

    private void analyzeTimeslotConflicts(List<Session> sessions, ConflictReport report) {
        for (int i = 0; i < sessions.size(); i++) {
            for (int j = i + 1; j < sessions.size(); j++) {
                Session session1 = sessions.get(i);
                Session session2 = sessions.get(j);
                
                // Conflit de salle
                if (session1.getRoom().getId().equals(session2.getRoom().getId())) {
                    report.addRoomConflict(session1, session2);
                }
                
                // Conflit d'enseignant
                if (session1.getTeacher().getId().equals(session2.getTeacher().getId())) {
                    report.addTeacherConflict(session1, session2);
                }
                
                // Analyser les conflits d'étudiants entre groupes
                analyzeStudentConflictsBetweenSessions(session1, session2, report);
            }
        }
    }

    private void analyzeStudentConflictsBetweenSessions(Session session1, Session session2, ConflictReport report) {
        // Récupérer les étudiants inscrits aux modules des deux sessions
        List<Long> students1 = enrollmentRepository.findByModuleId(session1.getModule().getId())
            .stream().map(e -> e.getStudent().getId()).toList();
        List<Long> students2 = enrollmentRepository.findByModuleId(session2.getModule().getId())
            .stream().map(e -> e.getStudent().getId()).toList();
        
        // Trouver les étudiants en commun
        List<Long> commonStudents = students1.stream()
            .filter(students2::contains)
            .toList();
        
        if (!commonStudents.isEmpty()) {
            report.addStudentConflict(session1, session2, commonStudents);
        }
    }

    /**
     * Classe pour encapsuler le rapport de conflits
     */
    public static class ConflictReport {
        private final List<String> roomConflicts = new java.util.ArrayList<>();
        private final List<String> teacherConflicts = new java.util.ArrayList<>();
        private final List<String> studentConflicts = new java.util.ArrayList<>();

        public void addRoomConflict(Session session1, Session session2) {
            roomConflicts.add(String.format("Conflit de salle %s : %s vs %s", 
                session1.getRoom().getName(), 
                session1.getModule().getName(), 
                session2.getModule().getName()));
        }

        public void addTeacherConflict(Session session1, Session session2) {
            teacherConflicts.add(String.format("Conflit d'enseignant %s %s : %s vs %s", 
                session1.getTeacher().getFirstName(), 
                session1.getTeacher().getLastName(),
                session1.getModule().getName(), 
                session2.getModule().getName()));
        }

        public void addStudentConflict(Session session1, Session session2, List<Long> commonStudents) {
            studentConflicts.add(String.format("Conflit d'étudiants (%d étudiants affectés) : %s vs %s", 
                commonStudents.size(),
                session1.getModule().getName(), 
                session2.getModule().getName()));
        }

        public List<String> getRoomConflicts() { return roomConflicts; }
        public List<String> getTeacherConflicts() { return teacherConflicts; }
        public List<String> getStudentConflicts() { return studentConflicts; }
        
        public boolean hasConflicts() {
            return !roomConflicts.isEmpty() || !teacherConflicts.isEmpty() || !studentConflicts.isEmpty();
        }
    }
}