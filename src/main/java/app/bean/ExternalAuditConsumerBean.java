package app.bean;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage; 

@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationLookup",
            propertyValue = "java:/jms/queue/MentorKEQueue"
        ),
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        )
    }
)
public class ExternalAuditConsumerBean implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage msg = (TextMessage) message;

            System.out.println(
                "ExternalAuditConsumerBean received: " + msg.getText()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error processing JMS message", e);
        }
    }
}
