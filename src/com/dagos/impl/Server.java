package com.dagos.impl;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.dagos.interfaces.MalwareScanner;
import com.dagos.interfaces.ReceiveNewHost;
import com.dagos.utils.HostManager;
import com.dagos.utils.MalwareDB;

public class Server implements MalwareScanner, ReceiveNewHost {
  private static MalwareDB malwareDB = new MalwareDB();
  private static HostManager hostManager = new HostManager();
  private static List<String> tempHostList = new ArrayList<>();
  private static String localHostIp = "192.168.183.128";

  public Server() {
  }

  public boolean scanForMalware(String fileName) throws RemoteException {
    return malwareDB.searchForSignature(fileName);
  }

  public boolean joinNewHost(String host) throws RemoteException {
    return hostManager.createNewConnection(host);
  }

  private static void InitializeServer() {
    tempHostList.add("192.168.183.129");
    tempHostList.add("192.168.183.130");

    try {
      Server obj1 = new Server();
      Server obj2= new Server();
      MalwareScanner stub1 = (MalwareScanner) UnicastRemoteObject.exportObject(obj1, 8080);
      ReceiveNewHost stub2 = (ReceiveNewHost) UnicastRemoteObject.exportObject(obj2, 8081);

      // Bind the remote object's stub in the registry
      Registry registry = LocateRegistry.getRegistry();
      registry.bind("MalwareScanner", stub1);
      registry.bind("ReceiveNewHost", stub2);

      System.err.println("Server ready.");
    } catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();
    }
  }

  private static void ConnectWithOtherServers() {
    try {
      for (String host : tempHostList) {
        Registry registry = LocateRegistry.getRegistry(host);
        ReceiveNewHost stub = (ReceiveNewHost) registry.lookup("ReceiveNewHost");
        boolean response = stub.joinNewHost(localHostIp);
      }
    } catch (Exception e) {
      System.err.println("Exception in connecting other hosts: " + e.toString());
      e.printStackTrace();
    }
  }

  private static void ExecuteScanning() {
    java.util.Scanner scanner = new java.util.Scanner(System.in);
    System.out.println("Enter filename to scan. Enter 'exit' to quit");
    String fileName = scanner.nextLine();

    while (!fileName.equalsIgnoreCase("exit")) {
      if (fileName.isEmpty()) {
        System.out.println("Enter valid file name!");
      } else {
        try {
          File file = new File(fileName);
          if (file.exists()) {
            System.out.println("Scanning file " + fileName + " for malware.");
            boolean localScanResult = malwareDB.searchForSignature(fileName);
            System.out.println("Local scan result: " + localScanResult);

            try {
              for (String host : hostManager.getHostList()) {
                Registry registry = LocateRegistry.getRegistry(host);
                MalwareScanner stub = (MalwareScanner) registry.lookup("MalwareScanner");
                boolean response = stub.scanForMalware(fileName);
                System.out.println("Scan result from " + host + ": " + response);
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
    if (args.length == 1) {
      localHostIp = args[0];
    }

    System.out.println("Local host ip address: " + localHostIp);
    System.setProperty("java.rmi.server.hostname", localHostIp);

    InitializeServer();
    System.out.println("Press enter to continue");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();

    ConnectWithOtherServers();
    ExecuteScanning();
  }
}