package io.kubernetes.client.examples;

import com.google.common.io.ByteStreams;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Attach;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * A simple example of how to use the Java API
 * <p>
 * <p>Easiest way to run this: mvn exec:java
 * -Dexec.mainClass="io.kubernetes.client.examples.AttachExample"
 * <p>
 * <p>From inside $REPO_DIR/examples
 */
public class AttachExample {
    public static void main(String[] args) throws IOException, ApiException, InterruptedException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        Attach attach = new Attach();
        final Attach.AttachResult result = attach.attach("default", "nginx-4217019353-k5sn9", true);

        new Thread(
                new Runnable() {
                    public void run() {
                        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                        OutputStream output = result.getStandardInputStream();
                        try {
                            while (true) {
                                String line = in.readLine();
                                output.write(line.getBytes());
                                output.write('\n');
                                output.flush();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                })
                .start();

        new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            ByteStreams.copy(result.getStandardOutputStream(), System.out);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                })
                .start();

        Thread.sleep(10 * 1000);
        //result.close();
        System.exit(0);
    }
}