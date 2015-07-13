# Compiler and archive tool
JAVAC := javac
JAR := jar cfm

# Sources and targets
SRC := src
BIN := bin
SOURCEPATH := $(SRC)/main/java
MAIN := $(SOURCEPATH)/tetris/gui/TetrisFrame.java
MANIFEST := MANIFEST.MF
JARTARGET := tetris.jar

# Phony targets
.PHONY: all cleanbin clean

all:
	mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) -sourcepath $(SOURCEPATH) $(MAIN)
	$(JAR) $(JARTARGET) $(MANIFEST) -C $(BIN) .

cleanbin:
	rm -rf $(BIN)

clean:
	rm -rf $(BIN) $(JARTARGET)
