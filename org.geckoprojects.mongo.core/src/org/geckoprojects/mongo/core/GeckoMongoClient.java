package org.geckoprojects.mongo.core;

import com.mongodb.client.MongoClient;

public interface GeckoMongoClient extends MongoClient {


	String mongoHost() ;

	String clientIdent();
}