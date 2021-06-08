package Index;

/**
 * Contains enumeration classes for the different types of state
 * that can exist for objects in an Index.
 */
public class State{
    
    /**
     * Represents the state of files with respect to the operations that can be performed on them.
     */
    public enum OperationState{
        // states
        STORE_IN_PROGRESS("Store In Progress"),
        STORE_ACK_RECIEVED("Store Acknowledgement Recieved"),
        STORE_COMPLETE("Store Complete"),
        REMOVE_IN_PROGRESS("Remove In Progress"),
        REMOVE_ACK_RECIEVED("Remove Acknowledgement Recieved"),
        REMOVE_COMPLETE("Remove Complete"),
        IDLE("Idle");

        private String state;

        private OperationState(String state){
            this.state = state;
        }
    
        /**
         * Converts the state method to a string.
         * @return String equivalent of the state.
         */
        @Override
        public String toString(){
            return this.state;
        }
    }

    /**
     * Represents the state of the system with respect to rebalance operations
     */
    public enum RebalanceState{
        //states
        REBALANCE_IN_PROGRESS("Rebalance In Progress"),
        REBALANCE_ACK_RECIEVED("Rebalance Acknowledgement Recieved"),
        IDLE("Idle");

        private String state;

        private RebalanceState(String state){
            this.state = state;
        }
    
        /**
         * Converts the state method to a string.
         * @return String equivalent of the state.
         */
        @Override
        public String toString(){
            return this.state;
        }
    }
}