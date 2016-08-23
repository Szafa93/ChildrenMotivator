package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.Configuration;

public class ConfigurationFactory {
    public static Configuration create() {
        Configuration configuration = new Configuration();
        configuration.setFromEmail("");
        configuration.setMailPassword("");
        configuration.setMailUser("");
        configuration.setSmtpHost("");
        configuration.setSmtpPort("");
        configuration.setSslConnection(false);
        return configuration;
    }
}
