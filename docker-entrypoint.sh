#!/bin/sh

exec java -XX:+UseParallelGC -jar app.jar
