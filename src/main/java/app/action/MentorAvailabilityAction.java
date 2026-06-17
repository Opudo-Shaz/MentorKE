package app.action;

import app.bean.MentorAvailabilityBean;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.model.MentorAvailability;
import app.security.websecurity.MentorKeSecurity;
import app.utility.logging.AppLogger;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.util.List;

@ApplicationScoped
@Action(value = "mentor-availability", label = "Set Availability")
@RolesAllowed({"mentor"})
public class MentorAvailabilityAction extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(MentorAvailabilityAction.class);

    @Inject private MentorAvailabilityBean availabilityBean;
    @Inject private MentorKeSecurity       security;

    @ActionGetMethod("")
    @RolesAllowed({"mentor"})
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireRole("mentor");

        String userId = getUserId(request);

        try {
            Long mentorId = Long.parseLong(userId);
            List<MentorAvailability> slots = availabilityBean.getAvailability(mentorId);
            request.setAttribute("availabilitySlots", slots);
            logger.debug("Availability loaded for mentorId:{} slots:{}", mentorId, slots.size());

        } catch (Exception e) {
            logger.error("Failed to load availability: {}", e.getMessage(), e);
            request.setAttribute("error", "Could not load availability: " + e.getMessage());
        }

        forward(request, response, "/mentor-availability.jsp");
    }
}