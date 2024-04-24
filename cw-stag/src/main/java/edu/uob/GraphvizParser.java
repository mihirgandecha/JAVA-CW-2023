package edu.uob;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;

public class GraphvizParser {
    private final String entityFileName;
    private Path entityFilePath;
    private Parser p;
    private FileReader reader;
    private ArrayList<Graph> wholeDocument;
//    private ArrayList<Graph> sections;
    private ArrayList<Location> locationArrayList;

    public GraphvizParser(String entityFileName) throws FileNotFoundException {
        this.entityFileName = entityFileName;
        this.entityFilePath = Paths.get("config" + File.separator + entityFileName).toAbsolutePath();
        this.locationArrayList = new ArrayList<>();
    }

    public void setup() throws GameError, FileNotFoundException, ParseException {
        if (doesDOTFileExist()){
            reader = new FileReader(Paths.get("config" + File.separator + entityFileName).toAbsolutePath().toFile());
        }
        p = new Parser();
        p.parse(reader);
    }

    public boolean doesDOTFileExist(){
        File f = new File(String.valueOf(entityFilePath));
        if (f.exists() && !f.isDirectory()){
            return true;
        }
        return false;
    }

    public ArrayList<Graph> getWholeDocumentGraphList(){
        return p.getGraphs();
    }

    public String toString() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(entityFilePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n"); // Keep newline character for accurate comparison
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

}
