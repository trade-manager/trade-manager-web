package org.trade.core.lookup;

/**
 * @author Simon Allen
 */
public interface ILookupServiceProvider {
    /**
     * Get the appropriate ILookup.
     *
     * @param lookupName String
     * @param qualifier  LookupQualifier
     * @param optional   boolean
     * @return ILookup
     */
    ILookup getLookup(String lookupName, LookupQualifier qualifier, boolean optional) throws LookupException;

}
