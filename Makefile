# Nikita Kouevda
# 2013/11/01

# Compiler, archive tool, and options
JAVAC := javac
JAR := jar
JAROPTS := cfm

# Sources and targets
SOURCES := $(wildcard tetris/game/*.java) $(wildcard tetris/gui/*.java)
TARGETS := $(SOURCES:.java=.class)
MANIFEST := MANIFEST.MF
JARTARGET := tetris.jar

# All class files and their escaped versions, for commands
CLASSES := $(wildcard tetris/game/*.class) $(wildcard tetris/gui/*.class)
ESCCLASSES := $(subst $$,\$$,$(CLASSES))

# Phony targets
.PHONY: all jar clean

all: $(TARGETS)

%.class: %.java
	$(JAVAC) $+

jar: all
	$(JAR) $(JAROPTS) $(JARTARGET) $(MANIFEST) $(ESCCLASSES)

clean:
	rm $(ESCCLASSES) $(JARTARGET)
