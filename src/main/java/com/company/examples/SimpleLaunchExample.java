package com.company.examples;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;

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

    V1ObjectMeta metadata = new V1ObjectMeta();
    metadata.setName("kuard");

    V1Container container = new V1Container();
    container.setImage("gcr.io/kuar-demo/kuard-amd64:1");
    container.setName("kuard");

    V1ContainerPort port = new V1ContainerPort();
    port.setContainerPort(8080);
    port.setName("http");
    port.setProtocol("HTTP");
    container.addPortsItem(port);

    V1PodSpec podSpec = new V1PodSpec();
    podSpec.addContainersItem(container);

    V1Pod pod = new V1Pod();
    pod.setMetadata(metadata);
    pod.setSpec(podSpec);

    try {
      V1Pod result = apiInstance.createNamespacedPod(namespace, pod, pretty);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CoreV1Api#createNamespacedPod");
      e.printStackTrace();
    }
  }
}
