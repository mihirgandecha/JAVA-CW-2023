package edu.uob.DBCommands;

import java.io.IOException;
import edu.uob.DBParse.Parser;

public interface handleInterpret {

    void interpret (Parser parsedCmnd) throws IOException;
}
