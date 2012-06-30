JAVAC=javac
sources = $(wildcard *.java)
classes = $(sources:.java=.class)

default: all

all: $(classes)

clean:
	rm -f *.class

%.class: %.java
	$(JAVAC) $<
