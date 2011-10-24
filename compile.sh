#!/bin/bash
CLASSPATH=/usr/share/processing/lib/core.jar:\
/usr/share/processing/libraries/opengl/library/jogl-natives-linux-amd64.jar:\
/usr/share/processing/libraries/opengl/library/jogl.jar:\
/usr/share/processing/libraries/opengl/library/gluegen-rt.jar:\
/usr/share/processing/libraries/opengl/library/opengl.jar:\
/home/sapht/processing/sketchbook/libraries/oscP5/library/oscP5.jar
LIBRARYPATH=/usr/share/processing/libraries/opengl/library
JAVA=/opt/emul-linux-x86-java-1.6.0.22/bin/java
javac -cp $CLASSPATH:. CircleCollision.java && \
javac -cp $CLASSPATH:. Ball.java && \
$JAVA -Djava.library.path="$LIBRARYPATH" -cp $CLASSPATH:. CircleCollision
