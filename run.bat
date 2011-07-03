@echo off
title Saleen
java -Xmx1536m -Djava.library.path=./lib/native -cp bin;lib/jar/json.jar;lib/jar/commons-codec-1.4.jar;lib/jar/commons-compress-1.0.jar;lib/jar/junit-4.6.jar;lib/jar/jython.jar;lib/jar/luajava-1.1.jar;lib/jar/netty-3.2.4.jar;lib/jar/mysql-connector-java-5.1.15-bin.jar;lib/jar/slf4j-api-1.5.8.jar;lib/jar/slf4j-jdk14-1.5.8.jar;lib/jar/xpp3_min-1.1.4c.jar;lib/jar/xstream-1.3.1.jar; org.saleen.Server
pause
