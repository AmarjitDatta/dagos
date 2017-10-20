#!/bin/bash

ps ax | grep 'rmiregistry' | awk -F ' ' '{print $1}' | xargs sudo kill -9

#Run code
ps ax | grep 'com.dagos.impl.Server' | awk -F ' ' '{print $1}' | xargs sudo kill -9
