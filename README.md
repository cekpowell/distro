# COMP2207: Distributed Systems and Networks

## Distributed File Storage System

---

### Introduction

- The task of this coursework was to develop a distributed file storage system using Java Sockets.
- The system has one **Controller** and N Data Stores (**Dstore**s).
- The system supports multiple concurrent **Client**s sending **STORE**, **LOAD**, **LIST** and **REMOVE** requests. 
- Each file is replicated **R** times over different Dstores. 
- The Controller orchestrates Client requests and maintains an index with the allocation of files to Dstores, as well as the size of each stored file. 
- The client sends/recieves files directly to/from Dstores - which makes the system very scalable.
- The system is able to adapt to new Dstores joining and old Dstores leaving.
- For simplicity reasons, the processes all occur on the same machine, running on different ports.
- Files in the system are not organised in folders and/or sub-folders and filenames do not contain spaces.

---

### Breakdown of System Components

- The **Distributed File Storage System** is made-up of three components:
  - **Controller** : The server within the system - takes requests from clients and handles them accordingly.
  - **Dstore** : A data store unit that is connected to the system via the controller. Dstores recieve commands from the controller and store files accordingly.
  - **Client** : A client that makes use of the system. The client sends requests to the Controller which are handeled accordingly.

---

### Starting Processes

- The **Makefile** contained in the repo can be used to start the three system components.
- The Controller must be started **first** (as Client and Dstore must be able to connect to the Controller to start.)
- All processes will **log** messages they receive from other processes to the command line.

#### Controller

- Use the following command to start a Controller process:

``` assembly
make controller cport=<CPORT> r=<R> timeout=<TIMEOUT> rperiod=<RPERIOD>
```

- Where:
  - `CPORT` : The **port** that the Controller will be run on (the port it will listen for requests on).
  - `R` : The **replication factor** - the number of Dstores across which all files will be replicated onto.
    - The controller will not serve requests from clients unless at least `R` Dstores are currently connected.
  - `TIMEOUT` : The **timeout** period for requests sent by the Controller to Clients/Dstores.
  - `RPERIOD` : The **rebalance period** - the length of time between rebalancing operations.

#### Dstore

- Use the following command to start a **Dstore** process:

```assembly
make dstore port=<PORT> cport=<CPORT> timeout=<TIMEOUT> path=<PATH>
```

- Where:
  - `PORT` : The **port** the Dstore will listen for communication on (the port it will listen for requests on).
  - `CPORT` : The **port the Controller** is running on.
  - `TIMEOUT` : The **timeout** period for requests sent by the Dstore to the Controller/Clients.
  - `PATH` : The **path** (relative or absolute) for where the Dstore will store the files it recieves from clients.

#### Client

- Use the following command to start a **Client** process:

```assembly
make client cport=<CPORT> timeout=<TIMEOUT>
```

- Where:
  - `CPORT` : The **port the Controller** is running on.
  - `TIMEOUT` : The **timeout** period for requests sent by the Client to the Controller/Dstores.

---

### Usage

- Commands are input via the **command line interface** on the **Client process**.
- The system supports the use of four commands:
  - **STORE** : Used to store a new file onto the system.
  - **LOAD** : Used to gather a file stored on the system.
  - **LIST** : Used to gather a list of all files stored on the system.
  - **REMOVE** : Used to remove a file from the system.
- Any file referenced by the client is can be referenced as a **relative** or **absolute path**, and must include the **file extension**.

#### STORE

- The **STORE** command has the following syntax:

```assembly
STORE <filename> <filesize>
```

- Where:
  - `filename` : The **path** to the file to be stored.
  - `filesize` : The **size** of the file in bytes.

#### LOAD

- The **LOAD** command has the following syntax:

```assembly
LOAD <filename>
```

- Where:
  - `filename` : The **name** of the file being loaded.

#### LIST

- The **LIST** command has the following syntax:

```assembly
LIST
```

#### REMOVE

- The **REMOVE** command has the following syntax:

```assembly
REMOVE <filename>
```

- Where:
  - `filename` : The **name** of the file to be removed.

---



