package com.company.examples;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeAddress;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple example of how to use the Java API
 */
public class ListNodesFromConfig {
    private static final String PRETTY = "true";

    public static void main(String[] args) throws IOException {
        String configFile = "resources/conf/config";

        ApiClient client = Config.fromConfig(configFile);
        Configuration.setDefaultApiClient(client);

        CoreV1Api v1Api = new CoreV1Api();

        try {
            List<String> nodeIPs = new ArrayList<String>();

            V1NodeList list = v1Api.listNode(PRETTY, "", "", "", 30, false);
            for (V1Node node : list.getItems()) {
                List<V1NodeAddress> addresses = node.getStatus().getAddresses();
                for (V1NodeAddress address : addresses) {
                    if (address.getType().equals("InternalIP")) {
                        nodeIPs.add(address.getAddress());
                    }
                }
            }
            System.out.println("Cluster node IPs: " + nodeIPs);
        } catch (Exception e) {
            System.out.println("Could not retrieve cluster IPs:");
            e.printStackTrace();
        }
    }
}
