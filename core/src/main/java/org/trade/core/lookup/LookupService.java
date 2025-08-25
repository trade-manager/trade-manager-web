package org.trade.core.lookup;

import org.trade.core.ApplicationContextProvider;
import org.trade.core.persistent.TradeService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 */
public class LookupService {

    //
    // Private Attributes
    //
    private static final List<ILookupServiceProvider> _providers = new ArrayList<>();

    static {

        addLookupServiceProvider(new PropertyFileLookupServiceProvider());
        addLookupServiceProvider(new DBTableLookupServiceProvider(ApplicationContextProvider.getBean(TradeService.class)));
    }

    /**
     * Get the appropriate ILookup.
     *
     * @param lookupName String
     * @param qualifier  LookupQualifier
     * @param optional   boolean
     * @return ILookup
     */
    public static ILookup getLookup(String lookupName, LookupQualifier qualifier, boolean optional)
            throws LookupException {

        ILookup lookup = null;
        // Loop through the registered providers and find and try to find one
        // that can provide the lookup
        int providersSize = _providers.size();

        for (int i = 0; i < providersSize; i++) {

            lookup = _providers.get(i).getLookup(lookupName, qualifier, optional);

            if (null != lookup) {
                // Have found a ILookup - don't care if another provider can
                // provide it or not
                break;
            }
        }
        return lookup;
    }

    /**
     * Method addLookupServiceProvider.
     *
     * @param provider ILookupServiceProvider
     */
    public static void addLookupServiceProvider(ILookupServiceProvider provider) {

        if (!_providers.contains(provider)) {
            _providers.add(provider);
        }
    }

    /**
     * Method removeLookupServiceProvider.
     *
     * @param provider ILookupServiceProvider
     */
    public static void removeLookupServiceProvider(ILookupServiceProvider provider) {

        _providers.remove(provider);
    }
}
