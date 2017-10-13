package com.dagos.impl;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import com.dagos.interfaces.Hello;

public class Server implements Hello {
    public Server() {
    }

    public String sayHello() {
        return "Hello, world!";
    }

    private static void InitializeServer() {
        try {
            Server obj = new Server();
            Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Hello", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void InitializeClient(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        Scanner scanner = new Scanner(System.in);
        System.out.println("Send request to remote server?");
        System.out.println("Enter anything to continue and 'no' to exit");
        while (!scanner.nextLine().equalsIgnoreCase("no")) {
            try {
                Registry registry = LocateRegistry.getRegistry(host);
                Hello stub = (Hello) registry.lookup("Hello");
                String response = stub.sayHello();
                System.out.println("response from " + host + ": " + response);
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        System.setProperty("java.rmi.server.hostname", "172.16.45.128");
        InitializeServer();
        InitializeClient(args);
    }
}
