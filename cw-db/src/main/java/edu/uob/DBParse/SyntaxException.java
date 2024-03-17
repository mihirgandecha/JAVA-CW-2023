package edu.uob.DBParse;

import java.io.Serial;

public class SyntaxException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public String errorTag;
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
