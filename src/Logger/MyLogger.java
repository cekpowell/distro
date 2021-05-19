package Logger;

public class MyLogger {

    private MyLogger(){}

    public static void logError(String error){
        System.out.println("*ERROR* : " + error);
    }

    public static void logEvent(String event){
        System.out.println("#EVENT# : " + event);
    }
}
