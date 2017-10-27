package com.dagos.utils;

import com.dagos.interfaces.MalwareScanner;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ScannerThread implements Runnable {
  private String hostName = "";
  private String signature = "";

  public ScannerThread(final String hostName, final String signature) {
    this.hostName = hostName;
    this.signature = signature;
  }

  @Override
  public void run() {
    try {
      Registry registry = LocateRegistry.getRegistry(hostName);
      MalwareScanner stub = (MalwareScanner) registry.lookup("MalwareScanner");
      boolean response = stub.scanForMalware(signature);
      System.out.println("Scan result from " + hostName + ": " + response);
    }
    catch (Exception ex) {
      System.err.println("Client exception: " + ex.toString());
      ex.printStackTrace();
    }
  }
}
