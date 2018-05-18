package com.company.plugins;

import com.company.orchestration.SecretStoreFactory;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class KubernetesSecret {

  public static class KubernetesSecretFactory implements SecretStoreFactory {
    @Override
    public SecretStore getInstance() {
      return createInstance();
    }
  }
  private static KubernetesSecretImpl createInstance() {
    return new KubernetesSecretImpl();
  }

  private static class KubernetesSecretImpl implements SecretStoreFactory.SecretStore {
    private static KubernetesClient client;

    public KubernetesSecretImpl() {
      Config config = new ConfigBuilder().build();
      client = new AutoAdaptableKubernetesClient(config);
    }

    public boolean validateSecret(String namespace, String secretName, String key, String value) {
      try {
        String password = retrieveSecret(namespace, secretName, key);
        if (password != null && password.equals(value))
          return true;
        return false;
      } catch (KubernetesClientException ke) {
        log.error("Error validating secret");
        return false;
      }
    }

    public void storeSecret(String namespace, String secretName, String key, String value) {
      Map<String, String> data = new LinkedHashMap<>();
      Base64.Encoder encoder = Base64.getEncoder();

      data.put(key, encoder.encodeToString(value.getBytes()));
      try {
        if (checkSecretExists(namespace, secretName)) {
          client.secrets().inNamespace(namespace).withName(secretName).edit()
            .editMetadata()
            .withName(secretName)
            .endMetadata()
            .addToData(data)
            .done();
        } else {
          client.secrets().inNamespace(namespace).withName(secretName).createOrReplaceWithNew()
            .editOrNewMetadata()
            .withName(secretName)
            .endMetadata()
            .addToData(data)
            .done();
        }
      } catch(KubernetesClientException ke) {
        log.error("Error storing secret", ke);
      }
    }

    public String retrieveSecret(String namespace, String secretName, String key) {
      Secret secret;

      try {
        secret = client.secrets().inNamespace(namespace).withName(secretName).get();
        Map<String, String> data = secret.getData();

        String encodedPassword = data.get(key);
        if (encodedPassword != null) {
          byte[] decodedValue = Base64.getDecoder().decode(encodedPassword);
          return new String(decodedValue);
        }
        return null;
      } catch (KubernetesClientException ke) {
        log.error("Error validating secret");
        return null;
      }
    }

    public boolean checkSecretExists(String namespace, String secretName) {
      try {
        Secret secret;
        secret = client.secrets().inNamespace(namespace).withName(secretName).get();
        if (secret != null) {
          return true;
        }
        return false;
      } catch (KubernetesClientException ke) {
        log.error("Kubernetes client error retrieving secrets", ke);
        return false;
      }
    }

    public boolean checkSecretKeyExists(String namespace, String secretName, String key) {
      try {
        Secret secret;
        secret = client.secrets().inNamespace(namespace).withName(secretName).get();
        if (secret != null) {
          Map<String, String> data = secret.getData();
          if (data.containsKey(key)) {
            return true;
          }
        }
        return false;
      } catch (KubernetesClientException ke) {
        log.error("Kubernetes client error retrieving secrets", ke);
        return false;
      }
    }

  }
}
