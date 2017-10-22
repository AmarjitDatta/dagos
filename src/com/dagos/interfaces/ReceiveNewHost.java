package com.dagos.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReceiveNewHost extends Remote {
  boolean joinNewHost(String host) throws RemoteException;
}
