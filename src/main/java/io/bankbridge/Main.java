package io.bankbridge;

import io.bankbridge.provider.LocalProvider;
import io.bankbridge.provider.RemoteProvider;
import io.bankbridge.handler.RequestHandler;
import spark.Service;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

	public static void main(String[] args) throws Exception {
		
		port(8080);
		setupRemoteAPI();

		RequestHandler remoteProvider = new RequestHandler(new RemoteProvider());
		RequestHandler localProvider = new RequestHandler(new LocalProvider());
		
		get("/v1/banks/all", localProvider::handle);
		get("/v2/banks/all", remoteProvider::handle);
	}

	public static void setupRemoteAPI() {
		Service remote = Service.ignite().port(1234).threadPool(10);

		remote.get("/rbb", (request, response) ->{
//					Thread.sleep(1000);
					return "{\n" +
							"\"bic\":\"1234\",\n" +
							"\"countryCode\":\"GB\",\n" +
							"\"auth\":\"OAUTH\"\n" +
							"}";
				}
				);
		remote.get("/cs", (request, response) -> "{\n" +
				"\"bic\":\"5678\",\n" +
				"\"countryCode\":\"CH\",\n" +
				"\"auth\":\"OpenID\"\n" +
				"}");
		remote.get("/bes", (request, response) -> "{\n" +
				"\"name\":\"Banco de espiritu santo\",\n" +
				"\"countryCode\":\"PT\",\n" +
				"\"auth\":\"SSL\"\n" +
				"}");
	}
}