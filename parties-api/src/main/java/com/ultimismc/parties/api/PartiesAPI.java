package com.ultimismc.parties.api;

import com.ultimismc.parties.api.party.PartiesManager;

/**
 * @author AkramL
 * @since 1.0-BETA
 */
public interface PartiesAPI {

    /**
     * Gets the instance of parties manager.
     *
     * @return Wrapped Parties Manager.
     */
    PartiesManager getPartiesManager();

}
