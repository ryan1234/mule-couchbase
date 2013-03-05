
package org.couchbase.adapters;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.couchbase.CouchbaseConnector;
import org.mule.api.Capabilities;
import org.mule.api.Capability;
import org.mule.api.ConnectionManager;
import org.mule.api.MuleContext;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.config.PoolingProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@code CouchbaseConnectorConnectionManager} is a wrapper around {@link CouchbaseConnector } that adds connection management capabilities to the pojo.
 * 
 */
public class CouchbaseConnectorConnectionManager
    implements Capabilities, ConnectionManager<CouchbaseConnectorConnectionManager.ConnectionKey, CouchbaseConnectorLifecycleAdapter> , MuleContextAware, Initialisable
{

    /**
     * 
     */
    private String bucketName;
    /**
     * 
     */
    private String password;
    private String Uri;
    private static Logger logger = LoggerFactory.getLogger(CouchbaseConnectorConnectionManager.class);
    /**
     * Mule Context
     * 
     */
    private MuleContext muleContext;
    /**
     * Flow construct
     * 
     */
    private FlowConstruct flowConstruct;
    /**
     * Connector Pool
     * 
     */
    private GenericKeyedObjectPool connectionPool;
    protected PoolingProfile connectionPoolingProfile;

    /**
     * Sets Uri
     * 
     * @param value Value to set
     */
    public void setUri(String value) {
        this.Uri = value;
    }

    /**
     * Retrieves Uri
     * 
     */
    public String getUri() {
        return this.Uri;
    }

    /**
     * Sets connectionPoolingProfile
     * 
     * @param value Value to set
     */
    public void setConnectionPoolingProfile(PoolingProfile value) {
        this.connectionPoolingProfile = value;
    }

    /**
     * Retrieves connectionPoolingProfile
     * 
     */
    public PoolingProfile getConnectionPoolingProfile() {
        return this.connectionPoolingProfile;
    }

    /**
     * Sets password
     * 
     * @param value Value to set
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Retrieves password
     * 
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets bucketName
     * 
     * @param value Value to set
     */
    public void setBucketName(String value) {
        this.bucketName = value;
    }

    /**
     * Retrieves bucketName
     * 
     */
    public String getBucketName() {
        return this.bucketName;
    }

    /**
     * Sets flow construct
     * 
     * @param flowConstruct Flow construct to set
     */
    public void setFlowConstruct(FlowConstruct flowConstruct) {
        this.flowConstruct = flowConstruct;
    }

    /**
     * Set the Mule context
     * 
     * @param context Mule context to set
     */
    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }

    public void initialise() {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        if (connectionPoolingProfile!= null) {
            config.maxIdle = connectionPoolingProfile.getMaxIdle();
            config.maxActive = connectionPoolingProfile.getMaxActive();
            config.maxWait = connectionPoolingProfile.getMaxWait();
            config.whenExhaustedAction = ((byte) connectionPoolingProfile.getExhaustedAction());
        }
        connectionPool = new GenericKeyedObjectPool(new CouchbaseConnectorConnectionManager.ConnectionFactory(this), config);
    }

    public CouchbaseConnectorLifecycleAdapter acquireConnection(CouchbaseConnectorConnectionManager.ConnectionKey key)
        throws Exception
    {
        return ((CouchbaseConnectorLifecycleAdapter) connectionPool.borrowObject(key));
    }

    public void releaseConnection(CouchbaseConnectorConnectionManager.ConnectionKey key, CouchbaseConnectorLifecycleAdapter connection)
        throws Exception
    {
        connectionPool.returnObject(key, connection);
    }

    public void destroyConnection(CouchbaseConnectorConnectionManager.ConnectionKey key, CouchbaseConnectorLifecycleAdapter connection)
        throws Exception
    {
        connectionPool.invalidateObject(key, connection);
    }

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

    private static class ConnectionFactory
        implements KeyedPoolableObjectFactory
    {

        private CouchbaseConnectorConnectionManager connectionManager;

        public ConnectionFactory(CouchbaseConnectorConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        public Object makeObject(Object key)
            throws Exception
        {
            if (!(key instanceof CouchbaseConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            CouchbaseConnectorLifecycleAdapter connector = new CouchbaseConnectorLifecycleAdapter();
            connector.setUri(connectionManager.getUri());
            if (connector instanceof Initialisable) {
                connector.initialise();
            }
            if (connector instanceof Startable) {
                connector.start();
            }
            return connector;
        }

        public void destroyObject(Object key, Object obj)
            throws Exception
        {
            if (!(key instanceof CouchbaseConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            if (!(obj instanceof CouchbaseConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                ((CouchbaseConnectorLifecycleAdapter) obj).disconnect();
            } catch (Exception e) {
                throw e;
            } finally {
                if (((CouchbaseConnectorLifecycleAdapter) obj) instanceof Stoppable) {
                    ((CouchbaseConnectorLifecycleAdapter) obj).stop();
                }
                if (((CouchbaseConnectorLifecycleAdapter) obj) instanceof Disposable) {
                    ((CouchbaseConnectorLifecycleAdapter) obj).dispose();
                }
            }
        }

        public boolean validateObject(Object key, Object obj) {
            if (!(obj instanceof CouchbaseConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                return ((CouchbaseConnectorLifecycleAdapter) obj).isConnected();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return false;
            }
        }

        public void activateObject(Object key, Object obj)
            throws Exception
        {
            if (!(key instanceof CouchbaseConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            if (!(obj instanceof CouchbaseConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                if (!((CouchbaseConnectorLifecycleAdapter) obj).isConnected()) {
                    ((CouchbaseConnectorLifecycleAdapter) obj).connect(((CouchbaseConnectorConnectionManager.ConnectionKey) key).getBucketName(), ((CouchbaseConnectorConnectionManager.ConnectionKey) key).getPassword());
                }
            } catch (Exception e) {
                throw e;
            }
        }

        public void passivateObject(Object key, Object obj)
            throws Exception
        {
        }

    }


    /**
     * A tuple of connection parameters
     * 
     */
    public static class ConnectionKey {

        /**
         * 
         */
        private String bucketName;
        /**
         * 
         */
        private String password;

        public ConnectionKey(String bucketName, String password) {
            this.bucketName = bucketName;
            this.password = password;
        }

        /**
         * Sets password
         * 
         * @param value Value to set
         */
        public void setPassword(String value) {
            this.password = value;
        }

        /**
         * Retrieves password
         * 
         */
        public String getPassword() {
            return this.password;
        }

        /**
         * Sets bucketName
         * 
         * @param value Value to set
         */
        public void setBucketName(String value) {
            this.bucketName = value;
        }

        /**
         * Retrieves bucketName
         * 
         */
        public String getBucketName() {
            return this.bucketName;
        }

        public int hashCode() {
            int hash = 1;
            hash = ((hash* 31)+ this.bucketName.hashCode());
            return hash;
        }

        public boolean equals(Object obj) {
            return ((obj instanceof CouchbaseConnectorConnectionManager.ConnectionKey)&&(this.bucketName == ((CouchbaseConnectorConnectionManager.ConnectionKey) obj).bucketName));
        }

    }

}
