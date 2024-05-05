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
        p = new GraphvizParser("config/basic-entities.dot");
        p.setup();
    }

    @Test
    public void testFileExistReturnsTrue() throws Exception {
        GraphvizParser graphvizParser = new GraphvizParser("config/basic-entities.dot");
        assertTrue(graphvizParser.doesDOTFileExist());
    }

    @Test
    public void testFileNotExistReturnsFalse() throws Exception {
        GraphvizParser graphvizParser = new GraphvizParser("config/fails.dot");
        assertFalse(graphvizParser.doesDOTFileExist());
    }

    @Test
    public void directoryShouldNotParseReturnFalse() throws Exception {
        GraphvizParser graphvizParser = new GraphvizParser("");
        assertFalse(graphvizParser.doesDOTFileExist());
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

    @Test
    void getPathsReturnsCorrectPaths(){
        Graph paths = p.getPaths();
        assertTrue(paths.toString().contains("paths"));
        assertTrue(paths.toString().contains("cabin"));
        assertTrue(paths.toString().contains("forest"));
        assertTrue(paths.toString().contains("cellar"));
        assertTrue(paths.toString().contains("->"));
    }

    //TODO High-Level Testing
//    String getPathsFromDOTFile(String dotFile) throws Exception {
//        GraphvizParser graphvizParser = new GraphvizParser(dotFile);
//        graphvizParser.setup();
//        Graph p = graphvizParser.getPaths();
//        return (p.getEdges().toString());
//    }

    @Test
    void pathToSetReturnsCorrectPathsForBasic() throws Exception {
        p.setPaths();
        Map<String, Location> map = p.getGameMap();
        Location cabin = map.get("cabin");
        Location forest = map.get("forest");
        Location cellar = map.get("cellar");
        assertTrue(cabin.pathTo.contains("forest"));
        assertTrue(forest.pathTo.contains("cabin"));
        assertTrue(cellar.pathTo.contains("cabin"));
    }

    @Test
    void pathToSetReturnsCorrectPathsForExtended() throws Exception {
        p = new GraphvizParser("config/extended-entities.dot");
        p.setup();
        p.setPaths();
        Map<String, Location> map = p.getGameMap();
        Location cabin = map.get("cabin");
        Location forest = map.get("forest");
        Location cellar = map.get("cellar");
        Location riverbank = map.get("riverbank");
        Location clearing = map.get("clearing");
        assertTrue(cabin.pathTo.contains("forest"));
        assertTrue(forest.pathTo.contains("cabin"));
        assertTrue(cellar.pathTo.contains("cabin"));
        assertTrue(forest.pathTo.contains("riverbank"));
        assertTrue(riverbank.pathTo.contains("forest"));
        assertTrue(clearing.pathTo.contains("riverbank"));
    }
}