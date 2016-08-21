package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Configuration;
import pl.szafraniec.ChildrenMotivator.repository.ConfigurationRepository;
import pl.szafraniec.ChildrenMotivator.services.ConfigurationService;

@Component
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Override
    public Configuration getConfiguration() {
        if (configurationRepository.count() == 0) {
            configurationRepository.saveAndFlush(new Configuration());
        }
        return configurationRepository.findAll().get(0);
    }
}
