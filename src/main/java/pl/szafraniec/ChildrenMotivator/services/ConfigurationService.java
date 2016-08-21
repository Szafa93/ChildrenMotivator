package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.Configuration;

public interface ConfigurationService {
    Configuration getConfiguration();

    Configuration editConfiguration(String fromEmail, String smtpHost, String smtpPort, String mailUser,
            String mailPassword, boolean sslConnection);
}
