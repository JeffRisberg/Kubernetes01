package com.company.examples;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.V1PersistentVolumeClaim;
import io.kubernetes.client.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.proto.Resource;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Watch events for a Pod Starting or stopping
 */
public class WatchEvents {
  private static ExecutorService executorService = Executors.newSingleThreadExecutor();

  public static void main(String[] args) throws IOException {
    // K8S_NAMESPACE defaults to empty
    String namespace = System.getenv("K8S_NAMESPACE");
    if (namespace == null || "".equals(namespace)) {
      namespace = "default";
    }

    Quantity maxClaims = Quantity.fromString("150Gi");
    Quantity totalClaims = Quantity.fromString("0Gi");

    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);
    //client.setDebugging(true);

    String apiServer = client.getBasePath();
    System.out.format("%nconnecting to API server %s %n%n", apiServer);

    client.getHttpClient().setReadTimeout(0, TimeUnit.SECONDS);
    CoreV1Api api = new CoreV1Api(client);
    V1PersistentVolumeClaimList list = null;
    try {
      list = api.listNamespacedPersistentVolumeClaim(namespace, null, null, null, null, null, null, null, null, null);
    } catch (ApiException apie) {
      System.err.println("Exception when calling CoreV1Api#listNamespacedPersistentVolumeClaim");
      apie.printStackTrace();
      System.exit(1);
    }
    printPVCs(list);

    // parse watched events
    System.out.format("%n----- PVC Watch (max total claims: %s) -----", maxClaims.toSuffixedString());
    try {
      Watch<V1PersistentVolumeClaim> watch = Watch.createWatch(
        client,
        api.listNamespacedPersistentVolumeClaimCall(
          namespace, null, null, null, null, null, null, null, null, Boolean.TRUE, null, null),
        new TypeToken<Watch.Response<V1PersistentVolumeClaim>>() {
        }.getType()
      );
      // let's watch for total PVC sizes
      for (Watch.Response<V1PersistentVolumeClaim> item : watch) {
        V1PersistentVolumeClaim pvc = item.object;
        String claimSize = null;
        Resource.Quantity claimQuant = null;
        BigDecimal totalNum = null;

        switch (item.type) {
          case "ADDED":
            System.out.println(item);
            break;
          case "MODIFIED":
            System.out.format("%nMODIFIED: PVC %s", pvc.getMetadata().getName());
            break;
          case "DELETED":
            System.out.println(item);
            break;
        }
        System.out.format(
          "%nINFO: Total PVC is at %4.1f%% capacity (%s/%s)",
          (totalClaims.getNumber().floatValue() / maxClaims.getNumber().floatValue()) * 100,
          totalClaims.toSuffixedString(),
          maxClaims.toSuffixedString()
        );
      }

    } catch (ApiException apie) {
      System.err.println("Exception watching PersistentVolumeClaims");
      apie.printStackTrace();
      System.exit(1);
    }
  }

  public static void printPVCs(V1PersistentVolumeClaimList list) {
    System.out.println("----- PVCs ----");
    String template = "%-16s\t%-40s\t%-6s%n";
    System.out.format(template, "Name", "Volume", "Size");

    for (V1PersistentVolumeClaim item : list.getItems()) {
      String name = item.getMetadata().getName();
      String volumeName = item.getSpec().getVolumeName();
      Quantity size = item.getSpec().getResources().getRequests().get("storage");
      System.out.format(template, name, volumeName, size);
    }
  }
}
