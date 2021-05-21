# location of project source files and where to put class bytecode
SOURCE_PATH=src
SOURCES=${SOURCE_PATH}/*.java
ODIR=out
CLASSPATH=${ODIR}

# Rules
build: 
	javac ${SOURCES} --source-path src --class-path ${CLASSPATH} -d ${ODIR} ${COMP_OPTIONS}

controller: 
	java -cp ${CLASSPATH} Controller.Controller $(cport) $(r) $(timeout) $(rperiod)

dstore:	
	java -cp ${CLASSPATH} Dstore.Dstore $(port) $(cport) $(timeout) $(path)

client:	
	java -cp ${CLASSPATH} Client.ClientTerminal $(cport) $(timeout)