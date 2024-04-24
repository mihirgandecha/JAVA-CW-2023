package edu.uob;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
    private List<Location> locationList;

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

    public String getLocationFromNode(Node node){
        String lName = node.getId().getId();
        return lName;
    }

    private Graph getRootGraph() {
        checkWholeDocument();
        return wholeDocument.get(0);
    }

    private boolean isNodeListEmpty(List<Node> nodeList) {
        return nodeList == null || nodeList.isEmpty();
    }

    public int getClusterNodeListSize(){
        List<Graph> clusters = getClusters();
        for (Graph cluster : clusters) {
            List<Node> nodesList = cluster.getNodes(false);
            return nodesList.size();
        }
        return 1;
    }

    //TODO change to private and include in setup()
    public Node loadLocationsAndEntities() throws Exception {
        Graph rootGraph = getRootGraph();
        List<Graph> clusters = getClusters();
//        int size = clusters.size();

        String locations = clusters.get(0).getAttribute("locations");
        List<Node> nodesList = clusters.get(0).getNodes(true);
        int size = nodesList.size();
        if (isNodeListEmpty(nodesList)) {
            throw new GameError("Node list is empty");
        }
        for (int i = 0; i < size; i++) {
            Node locationNode = nodesList.get(i);
            String description = locationNode.getAttribute("description");
            if (locationNode == null || description == null || description.isEmpty()) {
                continue;
            }
            if (locationNode == null || locationNode.getAttribute("description").isEmpty()){
                continue;
            }
            String locationName = locationNode.getId().getId();
            String locationDesc = locationNode.getAttribute("description");
            Location location = new Location(locationName, locationDesc);
            return locationNode;
        }
        return null;
    }

    private Location createLocationFromNode(Node node) throws Exception {
        String locationName = node.getId().getId();
        String description = node.getAttribute("description");
        return new Location(locationName, description);
    }

    //TODO null pointer exception
    private void addLocation(Location location) {
        locationList.add(location);
    }



    public void loadEntities() {

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
