package com.company.orchestration;

import com.company.interfaces.ISecretFactory;

public interface SecretStoreFactory extends ISecretFactory<SecretStoreFactory.SecretStore> {
  interface SecretStore {
    boolean validateSecret(String namespace, String secretName, String key, String value);

    void storeSecret(String namespace, String secretName, String key, String value);

    String retrieveSecret(String namespace, String secretName, String key);

    boolean checkSecretKeyExists(String namespace, String secretName, String key);

    boolean checkSecretExists(String namespace, String secretName);
  }
}