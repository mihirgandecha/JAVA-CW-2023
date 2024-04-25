package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphvizParserTest {
    GraphvizParser p;

    //TODO Test case is more on how the game work
    //TODO Split by low-level, medium, high-level (integration testing)
    //TODO !!!Integration tests: Take test you wrote and write situational based, eg character takes axe, chops tree, inventory should have log, HOWEVER map location should not have tree!

    @BeforeEach
    void setup() throws Exception {
        p = new GraphvizParser("basic-entities.dot");
        p.setup();
    }

    @Test
    public void testFileExistReturnsTrue() {
        GraphvizParser graphvizParser = new GraphvizParser("basic-entities.dot");
        assertTrue(graphvizParser.doesDOTFileExist());
    }

    @Test
    public void testFileNotExistReturnsFalse() {
        GraphvizParser graphvizParser = new GraphvizParser("fails.dot");
        assertFalse(graphvizParser.doesDOTFileExist());
    }

    @Test
    public void directoryShouldNotParseReturnFalse() {
        GraphvizParser graphvizParser = new GraphvizParser("");
        assertFalse(graphvizParser.doesDOTFileExist());
    }

    @Test
    public void testGetWholeGraphIsSizeOne() throws FileNotFoundException, ParseException {
        ArrayList<Graph> g = p.getWholeDocumentGraphList();
        assertEquals(1, g.size());
        assertThrows(IndexOutOfBoundsException.class, () -> p.getWholeDocumentGraphList().get(1));
    }

    @Test
    public void hashMapReturnsCorrectLocationNamesAndSize() throws Exception {
        p.setupGameMap();
        Map<String, Location> gameMap = p.getGameMap();
        assertEquals(4, gameMap.size());
        assertEquals("forest", gameMap.get("forest").getName());
        assertEquals("cabin", gameMap.get("cabin").getName());
        assertEquals("cellar", gameMap.get("cellar").getName());
        assertEquals("storeroom", gameMap.get("storeroom").getName());
    }
    //TODO Extra: .dot extension, if none add .dot extension and allow
    @Test
    public void dsReturnForestEntities() throws Exception {
        p.setupGameMap();
        Map<String, Location> gameMap = p.getGameMap();
//        System.out.println(gameMap.get("forest"));
//        assertEquals("No artefacts found", gameMap.get("forest").getArtefactsToString());
//        System.out.println(gameMap.get("forest").getCharactersToString());
//        System.out.println(gameMap.get("forest").getFurnitureToString());
        System.out.println(p.getClusters().get(1));
    }

    @Test
    void getPathsReturnsCorrectPaths(){
        Graph paths = p.getPaths();
        assertTrue(paths.toString().contains("paths"));
        assertTrue(paths.toString().contains("cabin"));
        assertTrue(paths.toString().contains("forest"));
        assertTrue(paths.toString().contains("cellar"));
        assertTrue(paths.toString().contains("->"));
    }

    String getPathsFromDOTFile(String dotFile) throws Exception {
        GraphvizParser graphvizParser = new GraphvizParser(dotFile);
        graphvizParser.setup();
        Graph p = graphvizParser.getPaths();
        return (p.getEdges().toString());
    }

    @Test
    void pathStringSplitSetup() throws Exception {
        Graph paths = p.getPaths();
//        int inputLen = getPathsFromDOTFile("basic-entities.dot").length;
//        System.out.println(getPathsFromDOTFile("basic-entities.dot"));
//        String[] expectedFirstPath = {"cabin", "forest"};
//        System.out.println(p.extractPathInformation("cabin -> forest;").toString());
//        assertTrue(p.extractPathInformation("cabin -> forest;").equals(expectedFirstPath));
    }

    @Test
    void pathToSetReturnsCorrectPaths() throws Exception {
        p.setupGameMap();
        Map<String, Location> map = p.getGameMap();
        Location cabin = map.get("cabin");
        Location forest = map.get("forest");
        Location cellar = map.get("cellar");
        p.setPaths();
        Graph paths = p.getPaths();
//        System.out.println(paths.getEdges().toString());
        System.out.println(paths.getEdges().get(0));
//        assertTrue(cabin.pathTo.contains("forest"));
//        assertTrue(forest.pathTo.contains("cabin"));
//        assertTrue(cellar.pathTo.contains("cabin"));
    }
}