#!/bin/bash
rm -f DenotexParser.java DenotexParserConstants.java DenotexParserTokenManager.java SimpleCharStream.java Token.java TokenMgrError.java
javacc Denotex.jj
javac -classpath /home/radar/src/java *.java
