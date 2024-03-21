package edu.uob.DBCmnd;

import java.io.IOException;
import java.io.Serial;

public class SyntaxException extends IOException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SyntaxException(String message){
        super("[ERROR]" + message);
    }

}
