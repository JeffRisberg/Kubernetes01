package com.company;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 */
@Slf4j
public class ResourceLocator {
  private static final String SEARCH_DNS_HOST_KEY = "aisera.search.host";
  private static final String SEARCH_DNS_PORT_KEY = "aisera.search.port";
  private static final String AS_LINKED_DOCUMENT = "aisera.data.document.asLinkedDocument";
  private static final String NLP_SVC_TYPE_KEY = "aisera.nlp.useSvc";

  private static final String SEARCH_DEFAULT_HOST = "localhost";
  private static final int SEARCH_DEFAULT_PORT = 9300;
  private static Properties props = new Properties();

  public static void registerProperties(InputStream is) {
    if (is != null) {
      try {
        synchronized (props) {
          props.load(is);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Depending on the orchestration, search cluster can be located as a different service
   * or in the same node as a separate micro service.
   *
   * @return
   */
  public static String getSearchHost() {
    return getResource(SEARCH_DNS_HOST_KEY).orElse(SEARCH_DEFAULT_HOST);
  }

  public static boolean doUseNLPSvc() {
    return ResourceLocator.getResourceAsBoolean(NLP_SVC_TYPE_KEY).orElse(false);
  }

  public static int getSearchPort() {
    return getResource(SEARCH_DNS_PORT_KEY).map((x) ->
        Integer.parseInt(x)).orElse(SEARCH_DEFAULT_PORT);
  }

  /**
   * Determines if the basic element of the graph/index is a linkedContent or Content
   * @return
   */
  public static final boolean useLinkedContentAsBasicElement() {
    return getResourceAsBoolean(AS_LINKED_DOCUMENT).orElse(true);
  }

  /**
   * Env takes precedence over properties
   *
   * @param key
   * @return
   */
  public static Optional<String> getResource(String key) {
    // Change all the key structure in Milestone_6 to use '_' instead of dots
    // This is a performance overhead, but making sticking to it for M5.
    // Order of resolution:
    //  get env value with modified key
    //  get env value with original key
    //  get properties value with original key

    String newKey = key.replaceAll("\\.", "_");
    String value = getEnvValue(newKey).orElseGet(() ->
            getEnvValue(key).orElseGet(() -> props.getProperty(key)));

    return (StringUtils.isEmpty(value)) ? Optional.empty() : Optional.of(value);
  }

  private static Optional<String> getEnvValue(String key) {
    String value = System.getenv(key);
    return ((value == null) || value.isEmpty()) ? Optional.empty() : Optional.of(value);
  }

  public static Optional<Boolean> getResourceAsBoolean(String key) {
    return getResource(key).map(x -> new Boolean(Boolean.valueOf(x)));
  }

  public static Optional<Integer> getResourceAsInt(String key) {
    return getResource(key).map(x -> new Integer(Integer.parseInt(x)));
  }

}
