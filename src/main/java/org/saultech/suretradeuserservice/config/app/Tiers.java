package org.saultech.suretradeuserservice.config.app;

import lombok.Data;

@Data
public class Tiers {
    private String currency;
    private Tier tier1;
    private Tier tier2;
    private Tier tier3;
    private Tier tier4;
    private Tier tier5;
    private Tier tier6;
}
