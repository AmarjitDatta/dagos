package com.dagos.utils;

import java.util.ArrayList;
import java.util.List;

public class HostManager {
  private static List<String> hostList = new ArrayList<>();

  public List<String> getHostList() {
    return hostList;
  }

  public void insertNewHost(String host) {
    hostList.add(host);
  }

  public boolean createNewConnection(final String host) {
    insertNewHost(host);
    System.out.println("Welcome host " + host);
    return true;
  }

  public boolean deleteOldConnection(final String host) {
    if (hostList.contains(host)) {
      hostList.remove(host);
    }
    return true;
  }
}
