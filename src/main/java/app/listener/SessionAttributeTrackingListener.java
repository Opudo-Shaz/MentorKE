package app.listener;

import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.annotation.WebListener;


@WebListener
public class SessionAttributeTrackingListener implements HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String attrName = se.getName();
        Object attrValue = se.getValue();

        // Don't log sensitive info in full detail, but track keys
        if ("password".equalsIgnoreCase(attrName)) {
            System.out.println("[SessionAttributeTrackingListener] Session attribute added: " + attrName + " (value: [REDACTED])");
        } else {
            System.out.println("[SessionAttributeTrackingListener] Session attribute added: " + attrName + " = " + attrValue);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        String attrName = se.getName();
        System.out.println("[SessionAttributeTrackingListener] Session attribute removed: " + attrName);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        String attrName = se.getName();
        Object oldValue = se.getValue();

        if ("password".equalsIgnoreCase(attrName)) {
            System.out.println("[SessionAttributeTrackingListener] Session attribute replaced: " + attrName + " (old: [REDACTED])");
        } else {
            System.out.println("[SessionAttributeTrackingListener] Session attribute replaced: " + attrName + " (old: " + oldValue + ")");
        }
    }
}

