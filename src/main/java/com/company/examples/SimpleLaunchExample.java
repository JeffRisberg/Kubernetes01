package com.company.examples;

import com.squareup.okhttp.Credentials;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.auth.ApiKeyAuth;
import io.kubernetes.client.auth.HttpBasicAuth;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class SimpleLaunchExample {
  private static final String PRETTY = "true";
  private static final Boolean includeUninitialized = true;
  private static final String DRYRUN = "All";

  public static void main(String[] args) throws IOException, ApiException {
    String configFile = "/Users/jeff/.kube/config";
    ApiClient client = Config.fromConfig(configFile);

    ApiKeyAuth BearerToken = (ApiKeyAuth) client.getAuthentication("BearerToken");

    if (BearerToken.getApiKey() == null) {
      System.out.println("Setting up AppKey");

      BearerToken.setApiKey(Credentials.basic(
        ((HttpBasicAuth) client.getAuthentications().get("BasicAuth")).getUsername(),
        ((HttpBasicAuth) client.getAuthentications().get("BasicAuth")).getPassword()));
    }
    client.setDebugging(true);
    Configuration.setDefaultApiClient(client);

    String namespace = "default";

    CoreV1Api apiInstance = new CoreV1Api();

    V1ObjectMeta metadata = new V1ObjectMeta();
    metadata.setName("kuard");

    V1Container container = new V1Container();
    container.setImage("gcr.io/kuar-demo/kuard-amd64:1");
    container.setName("kuard");

    V1ContainerPort port = new V1ContainerPort();
    port.setContainerPort(8080);
    port.setName("http");
    port.setProtocol("TCP");
    container.addPortsItem(port);

    V1PodSpec podSpec = new V1PodSpec();
    podSpec.addContainersItem(container);

    V1Pod pod = new V1Pod();
    pod.setMetadata(metadata);
    pod.setSpec(podSpec);

    try {
      V1Pod result = apiInstance.createNamespacedPod(namespace, pod, PRETTY, DRYRUN, "");
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CoreV1Api#createNamespacedPod");
      e.printStackTrace();
    }
  }
}
