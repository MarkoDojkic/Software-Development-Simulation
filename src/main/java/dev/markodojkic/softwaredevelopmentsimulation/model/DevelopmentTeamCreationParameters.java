package dev.markodojkic.softwaredevelopmentsimulation.model;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DevelopmentTeamCreationParameters {
    private int femaleDevelopersPercentage = 45;
    private int minimalDevelopersCount = 30;
    private int maximalDevelopersCount = 100;
    private int minimalDevelopersInTeamCount = 5;
    private int maximalDevelopersInTeamCount = 15;
    private boolean retainOld = true;
}
