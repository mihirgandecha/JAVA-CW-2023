package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GraphvizParser {
    private final Path entityFilePath;
    private final Parser p;
    public ArrayList<Graph> wholeDocument;
    public ArrayList<Graph> clusters;
    Map<String, Location> gameMap;
    private ArrayList<String> locationNames;
    private HashMap<String, GameEntity> entityList;
    public Node firstNode;

    public GraphvizParser(String entityFileName) throws Exception {
        this.entityFilePath = Paths.get("config" + File.separator + entityFileName).toAbsolutePath();
        this.p = new Parser();
        this.locationNames = new ArrayList<>();
        this.gameMap = new HashMap<>();
        setup();
    }

    public void setup() throws Exception {
        if (doesDOTFileExist()) {
            FileReader reader = new FileReader(this.entityFilePath.toFile());
            p.parse(reader);
            setWholeDocument();
            setClusterSubGraphs();
            setupGameMap();
//            setupGameEntities();
            setPaths();
        }
    }

    public boolean doesDOTFileExist() {
        File f = new File(String.valueOf(entityFilePath));
        return f.exists() && !f.isDirectory();
    }

    public ArrayList<Graph> getWholeDocumentGraphList() {
        return p.getGraphs();
    }

    public void setWholeDocument() {
        this.wholeDocument = p.getGraphs();
    }

    public void setClusterSubGraphs() {
        this.clusters = getClusters();
    }

    public ArrayList<Graph> getClusters() {
        return p.getGraphs().get(0).getSubgraphs();
    }

    private boolean checkNodeListIsEmpty(List<List<Node>> nodeList) {
        return nodeList == null || nodeList.isEmpty();
    }

    public boolean checkLocationHasNameAndDescription(Node node) {
        return node != null && node.getId() != null && !node.getId().getId().isEmpty();
    }

    public List<List<Node>> storeLocationClusters(List<Graph> graphs) {
        List<List<Node>> nodeLists = new ArrayList<>();
        for (Graph graph : graphs) {
            ArrayList<Node> nodes = graph.getNodes(true);
            nodeLists.add(nodes);
            if (firstNode == null && !nodes.isEmpty()) {
                firstNode = nodes.get(0);
            }
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
            ArrayList<Graph> subgraph = graphs.get(i).getSubgraphs();
            addArtefactsToLocation(l, subgraph);
            this.gameMap.put(locationNode.getId().getId(), l);
            this.locationNames.add(locationNode.getId().getId());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

    public Graph getPaths() {
        return getClusters().get(1);
    }

    public String[] extractPathInformation(String pathInfo) throws GameError {
        if (!pathInfo.contains("->") || !pathInfo.endsWith(";")) {
            throw new GameError("Path information is not in 'from -> to;' format");
        }
        String[] splitPath = pathInfo.split(" -> ");
        splitPath[1] = splitPath[1].substring(0, splitPath[1].length() - 1);
        return splitPath;
    }

    public void setPaths() throws GameError {
        ArrayList<Edge> edges = getPaths().getEdges();
        int size = edges.size();
        for (int i = 0; i < size; i++) {
            String[] path = extractPathInformation(edges.get(i).toString().replace("\n", ""));
            if(gameMap.containsKey(path[0])) {
                gameMap.get(path[0]).pathTo.add(path[1]);
            }
        }
    }

    private Location addArtefactsToLocation(Location location, ArrayList<Graph> nodes) throws GameError {
        int size = nodes.size();
        for(Graph graph : nodes) {
            for(Node node: graph.getNodes(true)){
                String type = graph.getId().toString();
//                ArrayList<Node> node1 = nodes.get(0).getNodes(true);
                String name = node.getId().getId().toString();
                String description = node.getAttribute("description");
                if (type.contains("artefact")) {
                    location.addArtefact(new Artefact(name, description));
                }
                else if (type.contains("furniture")) {
                    location.addFurniture(new Furniture(name,description));
                }
                else if (type.contains("character")) {
                    location.addCharacters(new Character(name,description));
                } else{
                    throw new GameError("Error adding entities");
                }
            }
            size++;
        }
        return location;
    }

}
