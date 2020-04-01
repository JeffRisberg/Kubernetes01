package com.company.plugins.orchestration.kubernetes;

import com.company.orchestration.SecretStoreFactory;

public class DummySecret {

  public static class DummySecretFactory implements SecretStoreFactory {
    @Override
    public SecretStoreFactory.SecretStore getInstance() {
      return createInstance();
    }
  }

  private static DummySecretImpl createInstance() {
    return new DummySecretImpl();
  }

  private static class DummySecretImpl implements SecretStoreFactory.SecretStore {

    public boolean validateSecret(String namespace, String secretName, String key, String value) {
      return true;
    }

    public void storeSecret(String namespace, String secretName, String key, String value) {
    }

    public String retrieveSecret(String namespace, String secretName, String key) {
      return key;
    }

    public boolean checkSecretExists(String namespace, String secretName) {
      return true;
    }

    public boolean checkSecretKeyExists(String namespace, String secretName, String key) {
      return true;
    }
  }
}
