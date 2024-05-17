package edu.uob.EntitiesFile;

import edu.uob.*;
import edu.uob.Character;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class LocationTest {

    public Location location;

    @BeforeEach
    public void setUp() throws Exception {
        location = new Location("Cabin", "A log cabin in the woods");
    }

    @Test
    public void testConstructor() throws Exception {
        assertEquals("Cabin", location.getName());
        assertEquals("A log cabin in the woods", location.getDescription());
        assertTrue(location.getArtefact().isEmpty());
        assertTrue(location.getCharacters().isEmpty());
        assertTrue(location.getFurniture().isEmpty());
    }

    @Test
    public void testAddAndRetrieveArtefact() throws Exception {
        Artefact axe = new Artefact("Axe", "A razor-sharp axe");
        location.addArtefact(axe);
        List<Artefact> artefacts = location.getArtefact();

        assertEquals(1, artefacts.size());
        assertEquals("Axe", artefacts.get(0).getName());
        Assertions.assertEquals(GameEntityType.ARTEFACT, artefacts.get(0).getType());
    }

    @Test
    public void testAddAndRetrieveCharacter() {
        Character elf = new Character("Elf", "A grumpy forest elf");
        location.addCharacters(elf);
        List<Character> characters = location.getCharacters();

        assertEquals(1, characters.size());
        assertEquals("Elf", characters.get(0).getName());
        assertEquals(GameEntityType.CHARACTER, characters.get(0).getType());
    }

    @Test
    public void testAddAndRetrieveFurniture() {
        Furniture chair = new Furniture("Chair", "A comfortable wooden chair");
        location.addFurniture(chair);
        List<Furniture> furnitures = location.getFurniture();

        assertEquals(1, furnitures.size());
        assertEquals("Chair", furnitures.get(0).getName());
        assertEquals(GameEntityType.FURNITURE, furnitures.get(0).getType());
    }

    @Test
    public void testConstructorException() throws Exception {
        // Test constructor with invalid inputs
        new Location(null, "Missing name");
        new Location("No description", null);
    }

    @Test
    public void testAddNullArtefact() {
        location.addArtefact(null);

        assertEquals(1, location.getArtefact().size());
        assertNull(location.getArtefact().get(0));
    }

    @Test
    public void testAddNullCharacter() {
        location.addCharacters(null);

        assertEquals(1, location.getCharacters().size());
        assertNull(location.getCharacters().get(0));
    }

    @Test
    public void testAddNullFurniture() {
        location.addFurniture(null);

        assertEquals(1, location.getFurniture().size());
        assertNull(location.getFurniture().get(0));
    }
}
