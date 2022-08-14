package com.ultimismc.parties.api;

/**
 * The provider class of Parties API
 *
 * @author AkramL
 * @since 1.0-BETA
 */
public class ApiProvider {

    private static PartiesAPI api;

    /**
     * Gets the wrapped API to use.
     * @return Wrapped {@link PartiesAPI}
     */
    public static PartiesAPI getApi() {
        if (api == null)
            throw new IllegalStateException("Cannot access API when It's not provided yet.");
        return api;
    }

    /**
     * Provides the wrapped API object to use.
     *
     * @param api Wrapped API.
     */
    public static void provide(PartiesAPI api) {
        if (ApiProvider.api != null)
            throw new IllegalStateException("Cannot provide API since it's already provided.");
        ApiProvider.api = api;
    }

}
