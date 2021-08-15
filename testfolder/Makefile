# location of project source files and where to put class bytecode
SOURCE_PATH=src/*
SOURCES=${SOURCE_PATH}/*.java
ODIR=out
CLASSPATH=${ODIR}

# Rules
build: 
	javac ${SOURCES} --source-path src --class-path ${CLASSPATH} -d ${ODIR} ${COMP_OPTIONS}

controller: 
	java -cp ${CLASSPATH} DS.Controller.ControllerTerminal $(cport) $(r) $(timeout) $(rperiod)

dstore:	
	java -cp ${CLASSPATH} DS.Dstore.DstoreTerminal $(port) $(cport) $(timeout) $(path)

client:	
	java -cp ${CLASSPATH} DS.DSClient.DSClientTerminal $(cport) $