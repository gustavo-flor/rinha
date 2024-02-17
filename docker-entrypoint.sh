#!/bin/sh

exec java -XX:MaxRAMPercentage=80 -XX:+UseParallelGC -jar app.jar
