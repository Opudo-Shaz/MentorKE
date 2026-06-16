package app.bean;

import app.dao.MentorAvailabilityDAO;
import app.model.MentorAvailability;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Stateless
@Named("mentorAvailabilityBean")
public class MentorAvailabilityBean {

    @Inject
    private MentorAvailabilityDAO availabilityDAO;

    public List<MentorAvailability> getAvailability(Long mentorId) {
        return availabilityDAO.findByMentorId(mentorId);
    }

    public void saveAvailability(Long mentorId, List<Map<String, String>> slots) {
        // Clear existing
        availabilityDAO.deleteByMentorId(mentorId);

        // Save new slots
        for (Map<String, String> slot : slots) {
            String day = slot.get("dayOfWeek");
            String start = slot.get("startTime");
            String end = slot.get("endTime");
            boolean available = "true".equals(slot.get("isAvailable"));

            if (day != null && start != null && end != null) {
                MentorAvailability avail = new MentorAvailability();
                avail.setMentorId(mentorId);
                avail.setDayOfWeek(day);
                avail.setStartTime(LocalTime.parse(start));
                avail.setEndTime(LocalTime.parse(end));
                avail.setAvailable(available);
                availabilityDAO.save(avail);
            }
        }
    }
}