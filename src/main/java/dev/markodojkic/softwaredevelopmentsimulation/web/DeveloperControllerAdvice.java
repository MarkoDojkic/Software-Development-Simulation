package dev.markodojkic.softwaredevelopmentsimulation.web;

import ch.qos.logback.core.util.StringUtil;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.generateRandomYugoslavianUMCN;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.getPlaceOfBirthBasedUMCNPoliticalRegionCode;

@ControllerAdvice
public class DeveloperControllerAdvice {

    @ModelAttribute("formDeveloperPlaceholder")
    public Developer overrideDeveloperFields(Developer developer) {
        if (StringUtil.isNullOrEmpty(developer.getYugoslavianUMCN())) {
            developer.setYugoslavianUMCN(generateRandomYugoslavianUMCN(developer.isFemale(), false));
        }

        if (StringUtil.isNullOrEmpty(developer.getId())) {
            developer.setId(UUID.nameUUIDFromBytes(developer.getYugoslavianUMCN().getBytes(StandardCharsets.UTF_8)).toString());
        }

        if (StringUtil.isNullOrEmpty(developer.getPlaceOfBirth())) {
            int regionCode = Integer.parseInt(developer.getYugoslavianUMCN().substring(7, 9));
            developer.setPlaceOfBirth(getPlaceOfBirthBasedUMCNPoliticalRegionCode(regionCode));
        }

        return developer;
    }
}