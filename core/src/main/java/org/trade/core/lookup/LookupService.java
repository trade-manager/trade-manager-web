/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.core.lookup;

import java.util.Vector;

/**
 * @author Simon Allen
 */
public class LookupService {
    //
    // Private Attributes
    //
    private static Vector<ILookupServiceProvider> _providers = new Vector<ILookupServiceProvider>();

    static {
        addLookupServiceProvider(new PropertyFileLookupServiceProvider());
        addLookupServiceProvider(new DBTableLookupServiceProvider());
    }

    /**
     * Get the appropriate ILookup.
     *
     * @param lookupName String
     * @param qualifier  LookupQualifier
     * @param optional   boolean
     * @return ILookup
     * @throws LookupException
     */
    public static ILookup getLookup(String lookupName, LookupQualifier qualifier, boolean optional)
            throws LookupException {
        ILookup lookup = null;
        // Loop through the registered providers and find and try to find one
        // that can provide the lookup
        int providersSize = _providers.size();

        for (int i = 0; i < providersSize; i++) {
            lookup = _providers.elementAt(i).getLookup(lookupName, qualifier, optional);

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
            _providers.addElement(provider);
        }
    }

    /**
     * Method removeLookupServiceProvider.
     *
     * @param provider ILookupServiceProvider
     */
    public static void removeLookupServiceProvider(ILookupServiceProvider provider) {
        _providers.removeElement(provider);
    }
}
