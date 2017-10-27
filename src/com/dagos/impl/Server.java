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
import com.dagos.utils.ScannerThread;

public class Server implements MalwareScanner, ReceiveNewHost {
  private static MalwareDB malwareDB = new MalwareDB();
  private static HostManager hostManager = new HostManager();
  private static List<String> tempHostList = new ArrayList<>();
  private static String localHostIp = "192.168.183.128";

  public Server() {
  }

  public boolean scanForMalware(String signature) throws RemoteException {
    return malwareDB.searchForSignature(signature);
  }

  public boolean joinNewHost(String host) throws RemoteException {
    return hostManager.createNewConnection(host);
  }

  private static void InitializeServer() {
    tempHostList.add("192.168.183.129");
    tempHostList.add("192.168.183.130");

    try {
      Server obj1 = new Server();
      Server obj2 = new Server();
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
    System.out.print("Enter signature to scan: ");
    String signature = scanner.nextLine();

    while (!signature.equalsIgnoreCase("exit")) {
      if (signature.isEmpty()) {
        System.out.println("Signature invalid!");
      } else {
        /*Start time*/
        long startTime = System.currentTimeMillis();

        System.out.println("Searching for signature " + signature + " for malware.");

        for (String host : hostManager.getHostList()) {
          ScannerThread scannerThread = new ScannerThread(host, signature);
          scannerThread.run();
        }

        boolean localScanResult = malwareDB.searchForSignature(signature);
        System.out.println("Local scan result: " + localScanResult);

        /*End time*/
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Scan execution time: " + (elapsedTime/1000.0) + " sec");
      }
      System.out.print("Enter signature to scan: ");
      signature = scanner.nextLine();
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