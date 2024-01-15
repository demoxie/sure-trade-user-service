package org.saultech.suretradeuserservice.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.saultech.suretradeuserservice.constants.BaseRoutes.TIERS;

@RestController
@RequestMapping(name = TIERS, produces = "application/json", consumes = "application/json")
public class TierController {
}
