#java version
1.8.0_151

$mkdir -p dagos-application/code
$cd dagos-application/code

#Download code from github
git clone https://github.com/AmarjitDatta/dagos.git

cd dagos/src

#Compile code
javac com/dagos/impl/*.java
javac com/dagos/interfaces/*.java
javac com/dagos/utils/*.java

#Start RMI registry
rmiregistry &

#Run code
java com.dagos.impl.Server {ip address of the server}







