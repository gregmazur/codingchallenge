package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.model.BankDto;
import io.bankbridge.provider.ResourceProvider;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class RequestHandler {

	private final ResourceProvider resourceProvider;
	private final ObjectMapper objectMapper;

	public RequestHandler(ResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
		this.objectMapper = new ObjectMapper();
	}

	public String handle(Request request, Response response) {

		try {
			Set<BankDto> dtos = resourceProvider.getBanks().stream()
					.map(b -> new BankDto(b.getBic(), b.getName())).collect(Collectors.toSet());
			return objectMapper.writeValueAsString(dtos);
		} catch (JsonProcessingException e) {
			log.warn("malformed data",e);
			response.status(503);
			return "Service unavailable";
		}

	}

}
