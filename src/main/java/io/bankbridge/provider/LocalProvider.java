package io.bankbridge.provider;

import io.bankbridge.model.BankModel;
import io.bankbridge.model.BankModelList;
import io.bankbridge.util.ResourceReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class LocalProvider implements ResourceProvider{

    private final Set<BankModel> cache;

    public LocalProvider() throws IOException {
        BankModelList models = ResourceReader.readResources("banks-v1.json", BankModelList.class);

        cache = models.getBanks();
        log.trace(getClass() + " initialized");
    }

    @Override
    public Set<BankModel> getBanks() {
        return cache;
    }

}
