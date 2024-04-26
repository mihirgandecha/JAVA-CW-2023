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

    private List<List<Node>> storeLocationClusters(List<Graph> graphs) {
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
            // Load entities of each location
            ArrayList<Graph> graphArray = getClusters().get(i).getSubgraphs().get(i).getSubgraphs();
                for (Graph node : graphArray) {
                    addArtefactsToLocation(l, graphArray);
                    addCharactersToLocation(l, graphArray);
                    addFurnitureToLocation(l, graphArray);
                }
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

//    private void setupGameEntities() {
//        List<Graph> subgraphs = getClusters();
//        for (Graph subgraph : subgraphs) {
//            String name = this.locationNames.get(0);
//            if (gameMap.containsKey(name)) {
//                Location location = gameMap.get(name);
//                ArrayList<Node> nodes = subgraph.getNodes(true);
//                addArtefactsToLocation(location, nodes);
//                addFurnitureToLocation(location, nodes);
//                addCharactersToLocation(location, nodes);
//                this.locationNames.remove(name);
//            } else {
//                System.out.println("Warning: Location not found in game map: " + locationNames.get(0));
//            }
//        }
//    }

    private void addArtefactsToLocation(Location location, ArrayList<Graph> nodes) {
        ArrayList<Node> node1 = nodes.get(0).getNodes(true);
        if(node1.get(0).getId().toString().contains("artefact")){
            Artefact artefact = new Artefact(
                    node1.get(0).getId().getId(),
                    node1.get(0).getAttribute("description")
            );

        }
//        for(Node node: node1){
//            if (node.getId().toString().contains("artefact")){
//                Artefact artefact = new Artefact(
//                        node1.get(0).getId().getId(),
//                        node1.get(0).getAttribute("description")
//                );
//
//            }
//        }
//        int size = node1.size();
//        for(int i = 0; i < size; i++){
//            if(node1.get(i).toString().contains("artefact")){
//                Artefact artefact = new Artefact(
//                        node1.get(i).getId().getId(),
//                        node1.get(i).getAttribute("description")
//                );
//            }
//        }
//        if(node1.getId().toString().equalsIgnoreCase("artefact")){
//            Artefact artefact = new Artefact(
//                    node1.getId().getId(),
//                    node1.getAttribute("description")
//            );
//        }
//        for (Graph node : nodes) {
//            if ("artefact".equalsIgnoreCase(node.getNodes(true).get(0) .getAttribute("type"))) {
//                Artefact artefact = new Artefact(
//                        node.getId().getId(),
//                        node.getAttribute("description")
//                );
//                location.addArtefact(artefact);
//            }
//        }
    }

    // Method to add furniture to a location
    private void addFurnitureToLocation(Location location, ArrayList<Graph> nodes) {
        for (Graph node : nodes) {
            if ("furniture".equalsIgnoreCase(node.getAttribute("type"))) {
                Furniture furniture = new Furniture(
                        node.getId().getId(),
                        node.getAttribute("description")
                );
                location.addFurniture(furniture);
            }
        }
    }

    // Method to add characters to a location
    private void addCharactersToLocation(Location location, ArrayList<Graph> nodes) {
        for (Graph node : nodes) {
            if ("character".equalsIgnoreCase(node.getAttribute("type"))) {
                Character character = new Character(
                        node.getId().getId(),
                        node.getAttribute("description")
                );
                location.addCharacters(character);
            }
        }
    }

    private void createEntity(String entityName, String entityDescription){
        entityList.put("artefacts", new Artefact(entityName, entityDescription));
        entityList.put("furniture", new Furniture(entityName, entityDescription));
        entityList.put("characters", new Character(entityName, entityDescription));
    }

    private void addLocationEntities(ArrayList<Graph> entitiesSubGraph, Location newLocation){
        for (Graph entityGraph : entitiesSubGraph) {
            ArrayList<Node> entityNode = entityGraph.getNodes(false);
            for (Node entity : entityNode) {
                createEntity(entity.getId().getId(), entity.getAttribute("description"));
//                newLocation.addEntity(newEntity);
            }
        }
    }
}
