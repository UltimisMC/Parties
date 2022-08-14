package com.ultimismc.parties.velocity.wrapper;

import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.PartiesManager;

public class PartiesApiWrapper implements PartiesAPI {

    private final PartiesManagerWrapper partiesManager = new PartiesManagerWrapper();

    @Override
    public PartiesManager getPartiesManager() {
        return partiesManager;
    }
}