package com.company.examples;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleLaunchExample {
  private static final String PRETTY = "true";

  public static void main(String[] args) throws IOException, ApiException {
    ApiClient client = Configuration.getDefaultApiClient();
    Configuration.setDefaultApiClient(client);

    String namespace = "default";
    String pretty = "pretty_example";

    CoreV1Api apiInstance = new CoreV1Api();

    V1Pod body = new V1Pod();
    V1PodSpec podSpec = new V1PodSpec();
    body.setSpec(podSpec);

    List<V1Container> containers = new ArrayList<>();
    V1Container container = new V1Container();
    container.setImage("kurad/kuar-demo/kuar-am64:1");
    container.setName("kuard");

    podSpec.addContainersItem(container);

    try {
      V1Pod result = apiInstance.createNamespacedPod(namespace, body, pretty);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CoreV1Api#createNamespacedPod");
      e.printStackTrace();
    }
  }
}
