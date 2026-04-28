#!/bin/bash

# variables
BASEDIR=$(dirname "$(readlink -f "$0")")
NATIVE=${BASEDIR}/lib/native
NAME="myapp"

# setting VM option for native library
java -Djava.library.path=${NATIVE} -jar ./target/${NAME}.jar

