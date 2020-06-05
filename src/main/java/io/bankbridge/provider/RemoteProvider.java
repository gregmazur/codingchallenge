package io.bankbridge.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.model.BankModel;
import io.bankbridge.model.RemoteBankModel;
import io.bankbridge.util.ResourceReader;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RemoteProvider implements ResourceProvider {

    private final Map config;
    private final AsyncHttpClient client;
    private final ExecutorService executorService;
    private final ObjectMapper objectMapper;

    public RemoteProvider() throws IOException {
        config = ResourceReader.readResources("banks-v2.json", Map.class);
        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
                .setConnectTimeout(500).setReadTimeout(500);
        client = Dsl.asyncHttpClient(clientBuilder);
        executorService = Executors.newCachedThreadPool();
        objectMapper = new ObjectMapper();
        log.trace(getClass() + " initialized");
    }

    @Override
    public Set<BankModel> getBanks() {
        RequestPortion requestPortion = new RequestPortion(client, objectMapper, executorService);

        return requestPortion.retrieveBanks(config);
    }


    private static class RequestPortion {
        private final AsyncHttpClient client;
        private final ObjectMapper objectMapper;
        private final ExecutorService executorService;
        private final Counter counter;
        private static final Object EMPTY_OBJECT = new Object();

        public RequestPortion(AsyncHttpClient client, ObjectMapper objectMapper, ExecutorService executorService) {
            this.client = client;
            this.objectMapper = objectMapper;
            this.executorService = executorService;
            this.counter = new Counter();
        }

        public Set<BankModel> retrieveBanks(Map<String, String> config) {
            Map<BankModel, Object> models = new ConcurrentHashMap<>(config.size());
            config.forEach((key, value) -> getBank(key, value, models));
            while (counter.getValue() != config.size()){}
            return models.keySet();
        }

        private void getBank(String name, String url, Map<BankModel, Object> result) {
            Request request = Dsl.get(url).build();
            ListenableFuture<Response> listenableFuture = client
                    .executeRequest(request);
            listenableFuture.addListener(() -> {
                try {
                    Response response = listenableFuture.get();
                    RemoteBankModel model = readModel(response.getResponseBody());
                    result.put(new BankModel(model.getBic(), name, model.getCountryCode(), model.getAuth()), EMPTY_OBJECT);
                } catch (InterruptedException | ExecutionException | IOException e) {
                    log.warn("data not recieved for " + name + " @ " + url, e);
                    result.put(new BankModel("n/a", name, "n/a", "n/a"), EMPTY_OBJECT);
                } finally {
                    counter.increment();
                }
            }, executorService);

        }

        private RemoteBankModel readModel(String text) throws IOException {
            return objectMapper.readValue(text, RemoteBankModel.class);
        }
    }

    public static class Counter {
        private final AtomicInteger counter = new AtomicInteger(0);

        public int getValue() {
            return counter.get();
        }

        public void increment() {
            while (true) {
                int existingValue = getValue();
                int newValue = existingValue + 1;
                if (counter.compareAndSet(existingValue, newValue)) {
                    return;
                }
            }
        }
    }
}
