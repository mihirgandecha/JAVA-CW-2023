package edu.uob;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;

// 1. list for checking if location is true
//
public class Locations { //extends GameEntity - d.set for storing entities (furniture, character, artefacts)
    Parser p;
    FileReader fr;

    public Locations(File entitiesFile) throws Exception {
        p = new Parser();
        fr = new FileReader(entitiesFile);

    }

    boolean isFilePresent(){
        return true;
    }

    void setParser() throws ParseException {
        p.parse(fr);
    }


    public void parseEntities(){
        try{
            Parser parser = new Parser();
            FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

        } catch (FileNotFoundException fnfe) {
            System.out.println("FileNotFoundException was thrown when attempting to read basic entities file");
        } catch (ParseException pe) {
            System.out.println("ParseException was thrown when attempting to read basic entities file");
        }
    }

}
