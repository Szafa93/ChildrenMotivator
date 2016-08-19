package pl.szafraniec.ChildrenMotivator.services.impl;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Configuration;
import pl.szafraniec.ChildrenMotivator.repository.ConfigurationRepository;
import pl.szafraniec.ChildrenMotivator.services.EmailService;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Override
    public void sendEmail(String to, String subject, String message, byte[] file, String fileName) {
        Configuration configuration = configurationRepository.findAll().get(0);

        MultiPartEmail email = new MultiPartEmail();
        try {
            DataSource source = new ByteArrayDataSource(new ByteArrayInputStream(file), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            email.attach(source, fileName, "");

            email.setSubject(subject);
            email.setMsg(message);

            email.setAuthentication(configuration.getMailUser(), configuration.getMailPassword());
            email.setFrom(configuration.getFromEmail());
            email.addTo(to);
            if (configuration.isSslConnection()) {
                email.setSslSmtpPort(configuration.getSmtpPort());
                email.setSSLOnConnect(true);
            } else {
                email.setSmtpPort(Integer.parseInt(configuration.getSmtpPort()));
            }
            email.setHostName(configuration.getSmtpHost());
            email.send();
        } catch (EmailException | IOException e) {
            e.printStackTrace();
        }
    }
}
