package Server;

/**
 * Enumeration class for the type of Server object that can exist within the 
 * system.
 */
public enum ServerType {
    // types
    CONTROLLER("Controller"), // Controller sever
    DSTORE("Dstore"); // Dstore server

    private String serverType;

    private ServerType(String serverType){
        this.serverType = serverType;
    }

    /**
     * Converts the server type method to a string.
     * @return String equivalent of the server type.
     */
    @Override
    public String toString(){
        return this.serverType;
    }

    /**
     * Gathers the server type from the given string.
     * @param text The String form of the server type
     * @return The ServerType object for the server type.
     */
    public static ServerType fromString(String text) {
        for (ServerType type : ServerType.values()) {
            if (type.serverType.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}