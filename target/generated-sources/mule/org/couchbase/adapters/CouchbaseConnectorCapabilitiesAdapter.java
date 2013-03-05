
package org.couchbase.adapters;

import org.mule.api.Capabilities;
import org.mule.api.Capability;


/**
 * A <code>CouchbaseConnectorCapabilitiesAdapter</code> is a wrapper around {@link org.couchbase.CouchbaseConnector } that implements {@link org.mule.api.Capabilities} interface.
 * 
 */
public class CouchbaseConnectorCapabilitiesAdapter
    extends org.couchbase.CouchbaseConnector
    implements Capabilities
{


    /**
     * Returns true if this module implements such capability
     * 
     */
    public boolean isCapableOf(Capability capability) {
        if (capability == Capability.LIFECYCLE_CAPABLE) {
            return true;
        }
        if (capability == Capability.CONNECTION_MANAGEMENT_CAPABLE) {
            return true;
        }
        return false;
    }

}
