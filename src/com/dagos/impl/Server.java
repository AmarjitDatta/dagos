package com.dagos.impl;

import java.io.File;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dagos.interfaces.MalwareScanner;
import com.dagos.utils.MalwareDB;

public class Server implements MalwareScanner {
  private static MalwareDB malwareDB;
  private static List<String> hostList = new ArrayList<>();
  private static HashMap<String , MalwareScanner> stubMap = new HashMap<>();

  public Server() {
    hostList.add("dagos-server1");
    hostList.add("dagos-server2");
    hostList.add("dagos-server3");
  }

  public boolean scanForMalware(String fileName) {
    return malwareDB.searchForSignature(fileName);
  }

  private static void InitializeServer() {
    try {
      Server obj = new Server();
      MalwareScanner stub = (MalwareScanner) UnicastRemoteObject.exportObject(obj, 0);

      // Bind the remote object's stub in the registry
      Registry registry = LocateRegistry.getRegistry();
      registry.bind("MalwareScanner", stub);

      /*Setup malware database*/
      malwareDB = new MalwareDB();

      System.err.println("Server ready");
    } catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();
    }
  }

  private static void InitializeClient() {
    for (String host : hostList) {
      try {
        Registry registry = LocateRegistry.getRegistry(host);
        MalwareScanner stub = (MalwareScanner) registry.lookup("MalwareScanner");
        stubMap.put(host, stub);
        System.out.println("Connection with " + host + " ready.");
      } catch (Exception e) {
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
      }
    }
  }

  private static void executeScanning() {
    java.util.Scanner scanner = new java.util.Scanner(System.in);
    System.out.println("Enter filename to scan. Enter 'exit' to quit");
    String fileName = scanner.nextLine();

    while (!fileName.equalsIgnoreCase("exit")) {
      if (fileName.isEmpty()) {
        System.out.println("Enter valid file name!");
      } else {
        System.out.println("Searching for file " + fileName);
        try {
          File file = new File(fileName);
          if (file.exists()) {
            try {
              for (String host : hostList) {
                MalwareScanner stub = stubMap.get(host);
                boolean response = stub.scanForMalware(fileName);
                System.out.println("Response from " + host + "? Is " + fileName + " malware? " + response);
              }
            } catch (Exception e) {
              System.err.println("Client exception: " + e.toString());
              e.printStackTrace();
            }
          } else {
            System.out.println("File not found. Enter a valid file name.");
          }
        } catch (Exception ex) {
          System.out.println("File not found. Enter a valid file name.");
        }
      }
      fileName = scanner.nextLine();
    }
  }

  public static void main(String args[]) {
    String host = (args.length < 1) ? null : args[0];
    if (host == null || host.isEmpty()) {
      host = "192.168.183.128";
    }

    System.setProperty("java.rmi.server.hostname", host);

    InitializeServer();
    InitializeClient();
    executeScanning();
  }
}
