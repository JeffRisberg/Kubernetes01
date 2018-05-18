package com.company.examples;

import com.company.io.IOHelper;
import com.company.orchestration.SecretStoreFactory;
import io.kubernetes.client.ApiException;

import java.io.IOException;
import java.net.URL;

/**
 * Uses Plugins as well as SecretStore
 */
public class UseSecrets {
  private static final String PRETTY = "true";

  UseSecrets() {
    try {
      URL resource = UseSecrets.class.getResource("/config/kube-config");
      SecretStoreFactory.SecretStore secretStore = IOHelper.getSecretStore();

      String namespace = "default";
      String secretName = "secret1";
      String key = "alpha";
      String value = "elephant";
      secretStore.storeSecret(namespace, secretName, key, value);

      String x = secretStore.retrieveSecret(namespace, secretName, key);
      System.out.println(x);

      System.out.println(secretStore.validateSecret(namespace, secretName, key, value));

    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException, ApiException {
    new UseSecrets();
  }
}
