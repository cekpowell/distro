#########
# SETUP #
#########

SOURCE_PATH=src
SOURCES=${SOURCE_PATH}/DS/*/*.java ${SOURCE_PATH}/Network/*/*.java
ODIR=out
CLASSPATH=${ODIR}

#########
# RULES #
#########

test:
	java -cp ${CLASSPATH} Test

## Compiling (compiles into /out) ##
compile: 
	javac ${SOURCES} --source-path src/ --class-path ${CLASSPATH} -d ${ODIR} ${COMP_OPTIONS}

## Running (run as processes) ##
run-controller: 
	java -cp ${CLASSPATH} DS.Controller.ControllerTerminal $(cport) $(r) $(timeout) $(rperiod)

run-dstore:	
	java -cp ${CLASSPATH} DS.Dstore.DstoreTerminal $(port) $(cport) $(timeout) $(path)

run-client:	
	java -cp ${CLASSPATH} DS.DSClient.DSClientTerminal $(cport) $(timeout)

## Building (builds into .jar file in /build) ##
build-controller: compile
	cd out; \
	jar -cvfe Controller.jar DS.Controller.ControllerTerminal .; \
	mv Controller.jar ..
	mv Controller.jar build

build-dstore: compile
	cd out; \
	jar -cvfe Dstore.jar DS.Dstore.DstoreTerminal .; \
	mv Dstore.jar ..
	mv Dstore.jar build

build-client: compile
	cd out; \
	jar -cvfe DSClient.jar DS.DSClient.DSClientTerminal .; \
	mv DSClient.jar ..
	mv DSClient.jar build