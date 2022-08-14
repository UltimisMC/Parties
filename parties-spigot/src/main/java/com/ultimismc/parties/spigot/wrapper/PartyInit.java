package com.ultimismc.parties.spigot.wrapper;

import com.andrei1058.bedwars.BedWars;
import com.ultimismc.parties.spigot.integration.BedWarsIntegration;

public class PartyInit {

    public PartyInit() {
        BedWars.setParty(new BedWarsIntegration());
    }

}
