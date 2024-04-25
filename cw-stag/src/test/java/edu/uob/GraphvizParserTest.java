package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphvizParserTest {
    GraphvizParser p;

    @BeforeEach
    void setup() throws FileNotFoundException, ParseException {
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
        GraphvizParser graphvizParser = new GraphvizParser("basic-entities.dot");
        graphvizParser.setup();
        ArrayList<Graph> g = graphvizParser.getWholeDocumentGraphList();
        assertEquals(1, g.size());
        assertThrows(IndexOutOfBoundsException.class, () -> graphvizParser.getWholeDocumentGraphList().get(1));
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
        System.out.println(gameMap.get("forest"));
        assertEquals("No artefacts found", gameMap.get("forest").getArtefactsToString());
        System.out.println(gameMap.get("forest").getCharactersToString());
        System.out.println(gameMap.get("forest").getFurnitureToString());
    }
}