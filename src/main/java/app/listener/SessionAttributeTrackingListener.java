package app.listener;

import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.annotation.WebListener;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;


@WebListener
public class SessionAttributeTrackingListener implements HttpSessionAttributeListener {

    private static final Logger logger = AppLogger.getLogger(SessionAttributeTrackingListener.class);

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String attrName = se.getName();
        Object attrValue = se.getValue();

        // Don't log sensitive info in full detail, but track keys
        if ("password".equalsIgnoreCase(attrName)) {
            logger.debug("Session attribute added: {} (value: [REDACTED])", attrName);
        } else {
            logger.debug("Session attribute added: {} = {}", attrName, attrValue);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        String attrName = se.getName();
        logger.debug("Session attribute removed: {}", attrName);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        String attrName = se.getName();
        Object oldValue = se.getValue();

        if ("password".equalsIgnoreCase(attrName)) {
            logger.debug("Session attribute replaced: {} (old: [REDACTED])", attrName);
        } else {
            logger.debug("Session attribute replaced: {} (old: {})", attrName, oldValue);
        }
    }
}

