
package org.trade.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;
import java.util.Date;

/**
 * The Identity Service component. The identity service provides globally unique
 * identities for the application.
 *
 * @author Simon Allen
 */
public class IdentityService {
    private static InetAddress m_localHost = null;

    /**
     * Method create.
     *
     * @return String
     */
    public static String create() throws IdentityServiceException {
        // Obtain a current timestamp.
        Date date = new Date();
        // Get an identity unique within the local host.
        UID hostUniqueId = new UID();

        // Obtain the host name.
        if (m_localHost == null) {
            try {
                m_localHost = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new IdentityServiceException(e);
            }
        }

        if (null == m_localHost) {
            throw new IdentityServiceException(
                    "Unable to resolve hostname.  " + "Is your networking configured Properly?");
        }

        String hostName;

        hostName = m_localHost.getHostName();

        // Construct the identity and return it.
        String identity;

        identity = "AT-" + date + "-" + hostName + "-" + hostUniqueId;

        return (identity);
    }
}
