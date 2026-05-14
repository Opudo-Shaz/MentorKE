package app.bean;

import app.dao.MentorDAO;
import app.dao.MenteeDAO;
import app.model.Mentor;
import app.model.Mentee;
import app.utility.logging.AppLogger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * SessionMatchingBean - EJB Stateless bean for mentor-mentee matching logic
 * Handles algorithms to match mentors with mentees based on specialization, experience, etc.
 */
@Stateless
@Named("sessionMatchingBean")
public class SessionMatchingBean {

    private static final Logger logger = AppLogger.getLogger(SessionMatchingBean.class);

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

    public SessionMatchingBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }

    /**
     * Find mentors by specialization
     */
    public List<Mentor> findMentorsBySpecialization(String specialization) throws SQLException {
        logger.info("Finding mentors with specialization: {}", specialization);

        List<Mentor> allMentors = mentorDAO.getActiveMentors();
        List<Mentor> matchedMentors = new ArrayList<>();

        for (Mentor mentor : allMentors) {
            if (mentor.getSpecialization() != null && 
                mentor.getSpecialization().equalsIgnoreCase(specialization)) {
                matchedMentors.add(mentor);
            }
        }

        logger.info("Found {} mentors with specialization: {}", matchedMentors.size(), specialization);
        return matchedMentors;
    }

    /**
     * Find optimal mentor for a mentee based on field of study and experience
     * Returns the mentor with most experience in the field
     */
    public Mentor findOptimalMentor(Mentee mentee) throws SQLException {
        logger.info("Finding optimal mentor for mentee: {}", mentee.getId());

        String fieldOfStudy = mentee.getFieldOfStudy();
        List<Mentor> candidates = findMentorsBySpecialization(fieldOfStudy);

        if (candidates.isEmpty()) {
            logger.warn("No mentors found for field: {}", fieldOfStudy);
            return null;
        }

        // Sort by years of experience (descending)
        Mentor bestMatch = candidates.stream()
            .max(Comparator.comparingInt(m -> m.getYearsOfExperience() != null ? m.getYearsOfExperience() : 0))
            .orElse(null);

        if (bestMatch != null) {
            logger.info("Optimal mentor found: {} with {} years of experience", 
                bestMatch.getId(), bestMatch.getYearsOfExperience());
        }

        return bestMatch;
    }

    /**
     * Auto-match a mentee with the best available mentor
     * Updates the mentee's mentor_id field
     */
    public Mentor autoMatchMentee(String menteeId) throws SQLException {
        logger.info("Auto-matching mentee: {}", menteeId);

        Mentee mentee = menteeDAO.getMentee(menteeId);
        if (mentee == null) {
            logger.warn("Mentee not found: {}", menteeId);
            return null;
        }

        Mentor bestMatch = findOptimalMentor(mentee);

        if (bestMatch != null) {
            mentee.setMentorId(String.valueOf(bestMatch.getId()));
            menteeDAO.updateMentee(menteeId, mentee);
            logger.info("Mentee {} successfully matched with mentor {}", menteeId, bestMatch.getId());
            return bestMatch;
        } else {
            logger.warn("Could not find a suitable mentor for mentee: {}", menteeId);
        }

        return null;
    }

    /**
     * Find top N mentors by specialization
     */
    public List<Mentor> findTopMentorsBySpecialization(String specialization, int limit) throws SQLException {
        logger.info("Finding top {} mentors for specialization: {}", limit, specialization);

        List<Mentor> candidates = findMentorsBySpecialization(specialization);

        return candidates.stream()
            .sorted(Comparator.comparingInt((Mentor m) -> 
                m.getYearsOfExperience() != null ? m.getYearsOfExperience() : 0).reversed())
            .limit(limit)
            .toList();
    }

    /**
     * Check if mentor and mentee are compatible based on specialization
     */
    public boolean areMentorMenteeCompatible(String mentorId, String menteeId) throws SQLException {
        logger.info("Checking compatibility between mentor {} and mentee {}", mentorId, menteeId);

        Mentor mentor = mentorDAO.getMentor(mentorId);
        Mentee mentee = menteeDAO.getMentee(menteeId);

        if (mentor == null || mentee == null) {
            logger.warn("Mentor or mentee not found");
            return false;
        }

        boolean compatible = mentor.getSpecialization() != null && 
                            mentee.getFieldOfStudy() != null &&
                            mentor.getSpecialization().equalsIgnoreCase(mentee.getFieldOfStudy());

        logger.info("Compatibility check result: {}", compatible);
        return compatible;
    }
}
