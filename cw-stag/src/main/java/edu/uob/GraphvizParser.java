package edu.uob;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

public class GraphvizParser {
    private final String entityFileName;
    private Path entityFilePath;
    private Parser p;
    private FileReader reader;
    public ArrayList<Graph> wholeDocument;
    public ArrayList<Graph> clusters;
    private ArrayList<Location> locationArrayList;

    public GraphvizParser(String entityFileName) throws FileNotFoundException {
        this.entityFileName = entityFileName;
        this.entityFilePath = Paths.get("config" + File.separator + entityFileName).toAbsolutePath();
        this.p = new Parser();
        this.locationArrayList = new ArrayList<>();
    }

    //Takes .dot file and parses to load elements into entities:
    public void setup() throws GameError, FileNotFoundException, ParseException {
        if (doesDOTFileExist()){
            reader = new FileReader(this.entityFilePath.toFile());
            p.parse(reader);
            setWholeDocument();
        }
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

    public void setWholeDocument() {
        this.wholeDocument = p.getGraphs();
    }

    public boolean checkWholeDocument(){
        return (wholeDocument == null || wholeDocument.isEmpty());
    }

    public ArrayList<Graph> getWholeDocumentAsGraph(){
        return p.getGraphs();
    }

    public void setClusterSubGraphs() {
        this.clusters = getClusters();
    }

    public ArrayList<Graph> getClusters(){
        return p.getGraphs().get(0).getSubgraphs();
    }

    public boolean checkClusterSubGraphs(){
        return (clusters == null || clusters.isEmpty());
    }

    public Location loadLocationFromNode(Node node){

    }

    public String toString() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(entityFilePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

}
