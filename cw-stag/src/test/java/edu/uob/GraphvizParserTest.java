package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GraphvizParserTest {

    @BeforeEach
    void setup() throws GameError, FileNotFoundException, ParseException {
        GraphvizParser p = new GraphvizParser("basic-entities.dot");
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
}