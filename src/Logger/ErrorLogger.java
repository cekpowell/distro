package Logger;

public class ErrorLogger {

    private ErrorLogger(){}

    public static void logError(String error){
        System.out.println("*ERRROR* : " + error);
    }
}
