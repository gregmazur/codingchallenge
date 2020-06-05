package io.bankbridge.provider;

import io.bankbridge.model.BankModel;

import java.util.Set;

public interface ResourceProvider {
    Set<BankModel> getBanks();
}
