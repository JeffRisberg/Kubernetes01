package com.company.interfaces;

/**
 * Created by kobiruskin on 8/1/17.
 */
public class NoPluginFoundException extends Exception {

  private final String pluginName;
  private final String pluginType;

  public NoPluginFoundException(String pluginName, String pluginType) {
    this.pluginName = pluginName;
    this.pluginType = pluginType;
  }

  public String toString() {
    return "No plugin found for '" + pluginName + "' and type '" + pluginType + "'";
  }
}
