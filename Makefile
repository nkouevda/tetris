JAVAC := javac
JAR := jar cfm

srcdir := src
sourcepath := $(srcdir)/main/java
main := $(sourcepath)/tetris/gui/TetrisFrame.java
manifest := MANIFEST.MF
bindir := bin
tetris := tetris.jar

.PHONY: all cleanbin clean

all:
	mkdir -p $(bindir)
	$(JAVAC) -d $(bindir) -sourcepath $(sourcepath) $(main)
	$(JAR) $(tetris) $(manifest) -C $(bindir) .

cleanbin:
	rm -rf $(bindir)

clean:
	rm -rf $(bindir) $(tetris)
