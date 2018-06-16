
package com.company.examples;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

import java.io.IOException;

/**
 * A simple example of how to use the Java API
 */
public class ListPods {
    public static void main(String[] args) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list =
                api.listPodForAllNamespaces(null, null, null, null, 10, false);
        for (V1Pod pod : list.getItems()) {
            System.out.println(pod.getMetadata().getName());
        }
    }
}
