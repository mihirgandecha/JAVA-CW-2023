package edu.uob.DBCmnd;

import java.io.IOException;
import java.io.Serial;

public class SyntaxException extends IOException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String errorTag;
    private final String errorMsg;

    public SyntaxException(int Tag, String Msg){
        super(tagToString(Tag));
        this.errorTag = tagToString(Tag);
        this.errorMsg = Msg;
    }

    private static String tagToString(int Tag){
        //TODO remove OK
        String tagOk = "[OK]";
        String tagError = "[ERROR]";
        if (Tag == 0){
            return tagOk;
        }
        else{
            return tagError;
        }
    }

    public String getErrorTag(){
        return this.errorTag;
    }

    public String getErrorMsg(){
        return this.errorMsg;
    }

    @Override
    public String toString() {
        return getErrorTag();
    }
}
