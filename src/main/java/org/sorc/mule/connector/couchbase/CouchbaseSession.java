package org.sorc.mule.connector.couchbase;

import com.couchbase.client.CouchbaseClient;

public class CouchbaseSession {
	private CouchbaseClient client;
	
	public CouchbaseSession(CouchbaseClient client)
	{
		this.client = client;
	}
	
	public CouchbaseClient getClient()
	{
		return this.client;
	}
}
