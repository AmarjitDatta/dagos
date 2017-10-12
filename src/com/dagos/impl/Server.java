package com.dagos.impl;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.dagos.interfaces.Hello;

public class Server implements Hello {
	public Server() {		
	}
	
	public String sayHello() {
        return "Hello, world!";
    }

    public static void main(String args[]) {
        System.setProperty("java.rmi.server.hostname", "172.16.45.128");
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
}
