package edu.uob.DBParse;

import java.io.IOException;
import java.util.*;

public class CreateSyntax {

    void parse(Parser p) throws SyntaxException, IOException {
        ArrayList<String> cmdCreateTokens = p.getTokens();

        if (cmdCreateTokens.size() <= 2){
            throw new SyntaxException("[ERROR]", "CREATE command too stort");
        }

//        <List>String firstToken = cmdCreateTokens.get(0);
//        String secondToken = cmdCreateTokens.get(1);
//
//        if (p.checkIfUppercase(firstToken) && p.checkIfUppercase(secondToken)) {
//            throw new IOException("CREATE or DATABASE lowercase");
//        }
    }
}
