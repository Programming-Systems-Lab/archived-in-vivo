JNI_INCLUDE = -I/usr/lib/jvm/java-6-openjdk/include/ -I/usr/java/include/linux
CC_SHARED_FLAGS = -shared -fPIC

PREFIX = /Users/del/Desktop/thesis/
AJC = ajc -source 5 #-inpath $(PREFIX)

CORE = com/invite/core
CORE_JAVA = $(CORE)/Affinity.java $(CORE)/Forker.java $(CORE)/Pipe.java $(CORE)/PipeReader.java \
		$(CORE)/Stats.java $(CORE)/InsertTest.java $(CORE)/Sorter.java

CORE_DEPS = $(CORE_JAVA) $(CORE)/Forker.c $(CORE)/Affinity.c $(CORE)/Pipe.c

DRIVERS = com/invite/drivers
DRIVERS_DEPS = $(DRIVERS)/IntFields.java $(DRIVERS)/Runner.java $(DRIVERS)/TestTracking.java

CHANGE = com/invite/changetracking
CHANGE_DEPS = $(CHANGE)/DiffChain.java $(CHANGE)/DiffBundle.java $(CHANGE)/DiffValue.java 

all: $(DRIVERS_DEPS) $(CHANGE_DEPS) 
	$(AJC) $(DRIVERS_DEPS) $(CHANGE_DEPS) $(CORE)/Invite.aj

all-old: $(CORE_DEPS) $(DRIVERS_DEPS) $(CHANGE_DEPS) 
	$(AJC) $(CORE_JAVA) $(DRIVERS_DEPS) $(CHANGE_DEPS)
	javah -jni -d $(CORE) com.invite.core.Forker
	gcc $(CORE)/Forker.c $(CC_SHARED_FLAGS) -o $(CORE)/libforker.so $(JNI_INCLUDE)
	javah -jni -d $(CORE) com.invite.core.Affinity
	gcc $(CORE)/Affinity.c $(CC_SHARED_FLAGS) -o $(CORE)/libaffinity.so $(JNI_INCLUDE)
	javah -jni -d $(CORE) com.invite.core.Pipe
	gcc $(CORE)/Pipe.c $(CC_SHARED_FLAGS) -o $(CORE)/libpipe.so $(JNI_INCLUDE)

clean-core:
	rm -rf $(CORE)/*.class $(CORE)/*.so

drivers: $(DRIVERS_DEPS)
	$(AJC) $(DRIVERS_DEPS)

clean-drivers:
	rm -rf $(DRIVERS)/*.class

changetracking: $(CHANGE_DEPS)
	$(AJC) $(CHANGE_DEPS)

runner: all
	java com/invite/drivers/Runner

runner-old: all-old
	java com/invite/drivers/Runner

clean-changetracking:
	rm -rf $(CHANGE)/*.class

clean: clean-core clean-drivers clean-changetracking
	rm -rf *.class


