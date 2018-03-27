#!/bin/bash
java -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -jar lib/${project.name}-${project.version}.jar
