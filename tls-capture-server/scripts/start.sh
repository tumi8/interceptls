#!/bin/bash
java -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -jar lib/tls-capture-server-1.0.jar
