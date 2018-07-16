package com.company.orchestration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeployableObject {
  private long id;
  private int numReplicas;
  private String name;
  private String dockerContainerName;
  private String inputParams;
  private String description;
  private String namespace;
  private String cronSchedule;
  private HashMap<String, String> env;
  private List<String> command;
  private List<String> args;
  private DeploymentType deploymentType;
  private String completionHttpGetHost;
  private int completionHttpGetPort;
  private String completionHttpGetPath;
}
