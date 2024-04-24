package edu.uob;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    //private ArrayList<Location> locationArrayList;
    Map<String, Location> gameMap;

    public GraphvizParser(String entityFileName) throws FileNotFoundException {
        this.entityFileName = entityFileName;
        this.entityFilePath = Paths.get("config" + File.separator + entityFileName).toAbsolutePath();
        this.p = new Parser();
//        this.locationArrayList = new ArrayList<>();
        this.gameMap = new HashMap<>();
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

    public String getLocationFromNode(Node node){
        String lName = node.getId().getId();
        return lName;
    }

    private Graph getRootGraph() {
        checkWholeDocument();
        return wholeDocument.get(0);
    }

    private boolean checkNodeListIsEmpty(List<List<Node>> nodeList) {
        return nodeList == null || nodeList.isEmpty();
    }

    public List<Node> getNodesOfLocations () {
        return getClusters().get(0).getNodes(true);
    }

    public int getSizeOfLocationNodes (){
        return getNodesOfLocations().size();
    }

    //TODO check if line break is ok
    public boolean checkLocationHasNameAndDescription(Node node) {
        return node != null && node.getId() != null && !node.getId().getId().isEmpty();
    }

    private List<List<Node>> storeLocationClusters(List<Graph> graphs){
        List<List<Node>> nodeLists = new ArrayList<>();
        for (Graph graph : graphs) {
            ArrayList<Node> Node = graph.getNodes(true);
            nodeLists.add(Node);
        }
        return nodeLists;
    }
    //TODO change to private and include in setup()
    public void setupGameMap() throws Exception {
        List<Graph> graphs = getClusters().get(0).getSubgraphs();
        int locationCount = graphs.size();
        List<List<Node>> nodeLists = storeLocationClusters(graphs);
        if (checkNodeListIsEmpty(nodeLists)) throw new GameError("Node list is empty");
        for (int i = 0; i < locationCount; i++) {
            Node locationNode = nodeLists.get(i).get(0);
            if (!checkLocationHasNameAndDescription(locationNode)) {
                throw new GameError("Node " + locationNode.getId() + " has no name and description");
            }
            Location l = createLocationFromNode(locationNode);
            this.gameMap.put(locationNode.getId().getId(), l);
        }
    }

    private Location createLocationFromNode(Node node) throws Exception {
        return new Location(node.getId().getId(), node.getAttribute("description"));
    }

    public Map<String, Location> getGameMap() {
        return this.gameMap;
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
