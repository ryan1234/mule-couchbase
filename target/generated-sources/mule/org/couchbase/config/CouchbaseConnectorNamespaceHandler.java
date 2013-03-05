
package org.couchbase.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Registers bean definitions parsers for handling elements in <code>http://www.mulesoft.org/schema/mule/couchbase</code>.
 * 
 */
public class CouchbaseConnectorNamespaceHandler
    extends NamespaceHandlerSupport
{


    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after construction but before any custom elements are parsed. 
     * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
     * 
     */
    public void init() {
        registerBeanDefinitionParser("config", new CouchbaseConnectorConfigDefinitionParser());
        registerBeanDefinitionParser("get", new GetDefinitionParser());
        registerBeanDefinitionParser("store", new StoreDefinitionParser());
        registerBeanDefinitionParser("remove", new RemoveDefinitionParser());
    }

}
