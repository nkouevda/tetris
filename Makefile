# Compiler, archive tool, and options
JAVAC := javac
JAR := jar
JAROPTS := cfm

# Sources and targets
SOURCES := $(wildcard tetris/*/*.java)
TARGETS := $(SOURCES:.java=.class)
MANIFEST := MANIFEST.MF
JARTARGET := tetris.jar

# All class files and their escaped versions, for commands
CLASSES := $(wildcard tetris/*/*.class)
ESCCLASSES := $(subst $$,\$$,$(CLASSES))

# Phony targets
.PHONY: all jar cleanclass clean

all: $(TARGETS)

%.class: %.java
	$(JAVAC) $+

jar: all
	$(JAR) $(JAROPTS) $(JARTARGET) $(MANIFEST) $(ESCCLASSES)

cleanclass:
	rm -f $(ESCCLASSES)

clean:
	rm -f $(ESCCLASSES) $(JARTARGET)
