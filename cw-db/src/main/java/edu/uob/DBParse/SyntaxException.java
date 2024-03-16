package edu.uob.DBParse;

public class SyntaxException extends Exception {
    String errorTag;
    String errorMsg;

    public SyntaxException(String errorTag, String errorMsg){
        super(errorMsg);
        this.errorTag = errorTag;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return this.errorTag + " " + errorMsg;
    }
}
