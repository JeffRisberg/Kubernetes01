package com.company.io;

import com.company.ResourceLocator;
import com.company.interfaces.NoPluginFoundException;
import com.company.orchestration.SecretStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 */
public class IOHelper {
  private static final Logger logger = LoggerFactory.getLogger(IOHelper.class);
  private static final String OBJECT_STORE_PLUGIN_KEY = "aisera.io.objectstore.plugin";
  private static final String PUB_SUB_PLUGIN_KEY = "aisera.io.pubsub.plugin";
  private static final String NOTIFIER_PLUGIN_KEY = "aisera.io.notifier.plugin";
  private static final String KV_STORE_PLUGIN_KEY = "aisera.io.kvstore.plugin";
  private static final String GRAPH_STORE_PLUGIN_KEY = "aisera.datastore.graph.plugin";
  private static final String SQL_STORE_PLUGIN_KEY = "aisera.datastore.sql.plugin";
  private static final String INDEXER_PLUGIN_KEY = "aisera.datastore.indexer.plugin";
  private static final String SECRET_STORE_PLUGIN_KEY = "aisera.io.secretstore.plugin";

  static {
    ResourceLocator.registerProperties(
      IOHelper.class.getResourceAsStream("/config/aisera_plugin.properties"));
  }

  private static <T> T createFactoryObject(String osPluginClass, String accountName)
    throws IOException {
    try {
      Class<?> objectStoreClass = Class.forName(osPluginClass);
      Object process = objectStoreClass.newInstance();
      Class<?>[] argsTypes = {String.class};

      Method aMethod = process.getClass().getMethod("getInstance", argsTypes);
      return (T) aMethod.invoke(process, accountName);
    } catch (Throwable t) {
      throw new IOException("Failed to initialize/Invoke the plugin : " + osPluginClass, t);
    }
  }

  private static <T> T createFactoryServiceObject(String osPluginClass, String config)
    throws IOException {
    try {
      Class<?> objectStoreClass = Class.forName(osPluginClass);
      Object process = objectStoreClass.newInstance();
      Class<?>[] argsTypes = {String.class};

      Method aMethod = process.getClass().getMethod("getInstance", argsTypes);
      return (T) aMethod.invoke(process, config);
    } catch (Throwable t) {
      throw new IOException("Failed to initialize/Invoke the plugin : " + osPluginClass, t);
    }
  }

  private static <T> T createFactoryServiceObject(String osPluginClass)
    throws IOException {
    try {
      Class<?> objectStoreClass = Class.forName(osPluginClass);
      Object process = objectStoreClass.newInstance();
      Class<?>[] argsTypes = {};

      Method aMethod = process.getClass().getMethod("getInstance", argsTypes);
      return (T) aMethod.invoke(process);
    } catch (Throwable t) {
      throw new IOException("Failed to initialize/Invoke the plugin : " + osPluginClass, t);
    }
  }

  public static <T> T addTenantId(T obj, String tenantId) {
    Class<?>[] argTypes = {String.class};
    try {
      Method method = obj.getClass().getMethod("setTenantId", argTypes);
      method.invoke(obj, tenantId);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return (T) obj;
  }

  private static final String OBJECT_STORE_DEFAULT_INDIRECTION = "default";

  public static ObjectStoreFactory.ObjectStore getObjectStore()
    throws NoPluginFoundException, InstantiationError, IOException {
    return getObjectStore(OBJECT_STORE_DEFAULT_INDIRECTION);
  }

  /**
   * Get object store plugin
   *
   * @param accountName
   * @return
   * @throws NoPluginFoundException
   * @throws InstantiationError
   * @throws IOException
   */
  public static ObjectStoreFactory.ObjectStore getObjectStore(String accountName)
    throws NoPluginFoundException, InstantiationError, IOException {
    String osPluginClass = ResourceLocator.getResource(OBJECT_STORE_PLUGIN_KEY)
      .orElseThrow(() -> new NoPluginFoundException(OBJECT_STORE_PLUGIN_KEY, "ObjectStore"));
    return createFactoryObject(osPluginClass, accountName);
  }

  public static ObjectStoreFactory.ObjectStore getObjectStore(String osPluginClass, String accountName)
    throws NoPluginFoundException, InstantiationError, IOException {
    return createFactoryObject(osPluginClass, accountName);
  }

  public static SecretStoreFactory.SecretStore getSecretStore()
    throws NoPluginFoundException, InstantiationError, IOException {
    String mode = System.getenv("dev_mode");
    String osPluginClass;
    if (mode != null && mode.equals("dev")) {
      osPluginClass = "com.company.plugins.orchestration.kubernetes.DummySecret$DummySecretFactory";
      logger.info("SecretStore is in DEV mode");
    } else {
      osPluginClass = "com.company.plugins.orchestration.kubernetes.KubernetesSecret$KubernetesSecretFactory";
      logger.info("SecretStore is in PROD mode");
    }
    return createFactoryServiceObject(osPluginClass);
  }
}
