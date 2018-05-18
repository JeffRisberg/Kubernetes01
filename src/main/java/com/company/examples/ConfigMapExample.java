package com.company.examples;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.DoneableConfigMap;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;

import java.util.Date;

public class ConfigMapExample {

  public static void main(String[] args) throws InterruptedException {
    KubernetesClient client = null;

    try {
      Config config = new ConfigBuilder().build();
      client = new DefaultKubernetesClient(config);

      String namespace = null;
      if (args.length > 0) {
        namespace = args[0];
      }
      if (namespace == null) {
        namespace = client.getNamespace();
      }
      if (namespace == null) {
        namespace = "default";
      }

      String name = "configmap1";
      try {
        Resource<ConfigMap, DoneableConfigMap> configMapResource = client.configMaps().inNamespace(namespace).withName(name);

        ConfigMap configMap = configMapResource.createOrReplace(new ConfigMapBuilder().
          withNewMetadata().withName(name).endMetadata().
          addToData("todayIs", "" + new Date()).
          addToData("animalType", "elephant").
          build());

        System.out.println("Upserted ConfigMap at " + configMap.getMetadata().getSelfLink() + " with data " + configMap.getData());
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        Resource<ConfigMap, DoneableConfigMap> configMapResource = client.configMaps().inNamespace(namespace).withName(name);

        ConfigMap configMap = configMapResource.get();

        System.out.println("Read ConfigMap at " + configMap.getMetadata().getSelfLink() + " with data " + configMap.getData());
      } catch (Exception e) {
        e.printStackTrace();
      }

      System.out.println("done");
    } finally {
      client.close();
    }
  }
}
