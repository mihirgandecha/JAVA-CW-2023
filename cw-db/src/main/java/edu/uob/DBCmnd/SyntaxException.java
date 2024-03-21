package edu.uob.DBCmnd;

import java.io.IOException;
import java.io.Serial;

public class SyntaxException extends IOException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String errorTag;

    public SyntaxException(int Tag){
        super(tagToString(Tag));
        this.errorTag = tagToString(Tag);
    }

    private static String tagToString(int Tag){
        //TODO remove OK
        String tagError = "[ERROR]";
        if (Tag == 0){
            return null;
        }
        else{
            return tagError;
        }
    }

    public String getErrorTag(){
        return this.errorTag;
    }

    @Override
    public String toString() {
        return getErrorTag();
    }
}
