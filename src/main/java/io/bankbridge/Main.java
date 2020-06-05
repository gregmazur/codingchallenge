package io.bankbridge;

import io.bankbridge.provider.LocalProvider;
import io.bankbridge.provider.RemoteProvider;
import io.bankbridge.handler.RequestHandler;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

	public static void main(String[] args) throws Exception {
		
		port(8080);

		RequestHandler remoteProvider = new RequestHandler(new RemoteProvider());
		RequestHandler localProvider = new RequestHandler(new LocalProvider());
		
		get("/v1/banks/all", localProvider::handle);
		get("/v2/banks/all", remoteProvider::handle);
	}

}