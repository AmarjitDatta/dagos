#!/bin/bash

#Compile codes
javac com/dagos/impl/*.java
javac com/dagos/interfaces/*.java
javac com/dagos/utils/*.java

#Run rmi registry
rmiregistry &

#Run code
java com.dagos.impl.Server
