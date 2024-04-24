package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GraphvizParserTest {
    GraphvizParser p;

    @BeforeEach
    void setup() throws GameError, FileNotFoundException, ParseException {
        p = new GraphvizParser("basic-entities.dot");
        p.setup();
    }


    //100% coverage
    @Test
    public void testFileExistReturnsTrue() throws Exception {
        GraphvizParser graphvizParser = new GraphvizParser("basic-entities.dot");
        assertTrue(graphvizParser.doesDOTFileExist());
    }

    @Test
    public void testFileNotExistReturnsFalse() throws Exception {
        GraphvizParser graphvizParser = new GraphvizParser("fails.dot");
        assertFalse(graphvizParser.doesDOTFileExist());
    }

    @Test
    public void directoryShouldNotParseReturnFalse() throws Exception {
        GraphvizParser graphvizParser = new GraphvizParser("");
        assertFalse(graphvizParser.doesDOTFileExist());
    }

    //TODO Extra: .dot extension, if none add .dot extension and allow

    //Testing Setup
    @Test
    public void test() {
    }

    @Test
    public void testGetWholeGraphIsSizeOne() throws FileNotFoundException, GameError, ParseException {
        GraphvizParser graphvizParser = new GraphvizParser("basic-entities.dot");
        graphvizParser.setup();
        ArrayList<Graph> g = graphvizParser.getWholeDocumentGraphList();
        assertEquals(1, g.size());
        assertThrows(IndexOutOfBoundsException.class, () -> graphvizParser.getWholeDocumentGraphList().get(1));
    }


    @Test
    public void topLevelGraphHasKeywordsLayoutAndSplinesButClustersDoesNot() throws FileNotFoundException, GameError, ParseException {
        p.setWholeDocument();
        assertEquals(1, p.wholeDocument.size());
//        System.out.println(p.wholeDocument);
//        System.out.println("---------------");
//        System.out.println(p.wholeDocument.get(0).getSubgraphs());
        p.setClusterSubGraphs();
//        System.out.println(p.clusters);
//        assertTrue(p.wholeDocument.contains("locations"));
//        assertTrue(p.wholeDocument.contains("paths"));
//        boolean hasLocations = p.clusters.stream()
//                .anyMatch(graph -> graph.getType().equals("locations"));
//        boolean hasPaths = p.clusters.stream()
//                .anyMatch(graph -> graph.getType().equals("paths"));
//        assertTrue(hasLocations);
//        assertTrue(hasPaths);
//
//        assertTrue(p.clusters.contains("locations"));
//        assertTrue(p.clusters.contains("paths"));
    }


    @Test
    public void tester() throws Exception {
        p.loadLocationsAndEntities();
        System.out.println(p.loadLocationsAndEntities());

    }
}