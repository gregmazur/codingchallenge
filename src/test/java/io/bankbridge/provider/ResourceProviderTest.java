package io.bankbridge.provider;

import io.bankbridge.MockRemotes;
import io.bankbridge.model.BankModel;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

public class ResourceProviderTest extends MockRemotes {

    @Test
    public void test_localProvider() throws IOException {
        ResourceProvider local = new LocalProvider();
        Set<BankModel> modelSet = local.getBanks();
        assertEquals(1, modelSet.size());
        assertEquals("Name", modelSet.stream().findFirst().get().getName());
    }

    @Test
    public void test_remoteProvider() throws IOException {
        ResourceProvider remote = new RemoteProvider();
        Set<BankModel> modelSet = remote.getBanks();
        assertEquals(3, modelSet.size());
        assertEquals("n/a", modelSet.stream().filter(b -> b.getName().
                equals("Royal Bank of Boredom")).findFirst().get().getBic());
        assertEquals("n/a", modelSet.stream().filter(b -> b.getName()
                .equals("Banco de espiritu santo")).findFirst().get().getBic());
        assertEquals("5678", modelSet.stream().filter(b -> b.getName()
                .equals("Credit Sweets")).findFirst().get().getBic());
    }
}