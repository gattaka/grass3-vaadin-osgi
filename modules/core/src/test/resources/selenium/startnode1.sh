java -jar selenium-server-standalone-2.30.0.jar -role node -hub http://localhost:4444/grid/register -maxSession 20 -port 5555 -browser browserName=chrome,maxInstances=20,platform=LINUX
