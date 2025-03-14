package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExampleSTAGTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() throws Exception {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(10000000), () -> {
                    return server.handleCommand(command);
                },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    String randomiseCasing(String inFromGenerateRandomName) {
        StringBuilder randomiseCaseForName = new StringBuilder();
        Random random = new Random();
        for (char c : inFromGenerateRandomName.toCharArray()) {
            if (random.nextBoolean()) {
                randomiseCaseForName.append(java.lang.Character.toUpperCase(c));
            } else {
                randomiseCaseForName.append(java.lang.Character.toLowerCase(c));
            }
        }
        return randomiseCaseForName.toString();
    }

    @Test
    void testFileHandling() throws GameError {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("coin"), "Did not see the name of the current room in response to look");
    }




    @Test
    void testPlayerDeath() {
        String response;
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: fight with elf");
        sendCommandToServer("simon: health");
        sendCommandToServer("simon: fight with elf");
        sendCommandToServer("simon: health");
        response = sendCommandToServer("simon: fight with elf");
        response = response.toLowerCase();
        assertEquals("you died and lost all of your items, you must return to the start of the game\n", response);
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"));
    }

    @Test
    void testExampleScript() {
        String response = sendCommandToServer("simon: inv");
        assertEquals("inventory is empty\n", response.toLowerCase());

        //Initial look
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(Stream.of("cabin", "potion", "axe", "trapdoor", "forest").allMatch(response::contains));

        // Check inventory after picking axe
        response = sendCommandToServer("simon: inv");
        assertEquals("inventory is empty\n", response.toLowerCase());

        //Pickup Axe
        response = sendCommandToServer("simon: get axe");
        assertEquals("you picked up a axe\n", response.toLowerCase());

        // Check inventory after picking axe
        response = sendCommandToServer("simon: inv");
        assertTrue(response.toLowerCase().contains("axe"));

        //Look - check axe not in cabin location
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(Stream.of("cabin", "potion", "trapdoor", "forest").allMatch(response::contains));

        //Pickup Potion
        response = sendCommandToServer("simon: get potion");
        assertEquals("you picked up a potion\n", response.toLowerCase());

        //Look - check potion not in cabin location
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(Stream.of("cabin", "trapdoor", "forest").allMatch(response::contains));

        // Check inventory after picking potion with 'inventory'
        response = sendCommandToServer("simon: inv");
        assertTrue(response.toLowerCase().contains("potion"));

        //Goto - check player is moved
        response = sendCommandToServer("simon: goto forest");
        response = response.toLowerCase();
        assertTrue(Stream.of("forest", "key", "cabin").allMatch(response::contains));

        response = sendCommandToServer("simon: chop tree");
        assertEquals("you cut down the tree with the axe\n", response.toLowerCase());

        //Pickup Key - check key not in forest location
        response = sendCommandToServer("simon: get key");
        assertEquals("you picked up a key\n", response.toLowerCase());

        response = sendCommandToServer("simon: inv");
        //Goto cabin now having key
        response = sendCommandToServer("simon: goto cabin");
        response = response.toLowerCase();
        assertTrue(Stream.of("cabin", "trapdoor", "forest").allMatch(response::contains));

        //Check advanced Action: trapdoor can be opened as player holds key
        response = sendCommandToServer("simon: open trapdoor");
        assertEquals("you unlock the trapdoor and see steps leading down into a cellar\n", response.toLowerCase());

        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cellar"));

        //TODO need to remove trapdoor?
        response = sendCommandToServer("simon: goto cellar");
        response = response.toLowerCase();
        assertTrue(Stream.of("cellar", "elf", "cabin").allMatch(response::contains));
    }

    @Test
    void testBasicGameCommands() {
        String response;
        // Initial Look in the starting location
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"), "Look should reveal the cabin description.");
        assertTrue(response.toLowerCase().contains("axe"), "Axe should be visible in cabin.");
        assertTrue(response.toLowerCase().contains("potion"), "Potion should be visible in cabin.");

        // Pickup Axe
        response = sendCommandToServer("simon: get axe");
        assertTrue(response.toLowerCase().contains("you picked up a axe"));

        // Verify inventory contains the Axe
        response = sendCommandToServer("simon: inventory");
        assertTrue(response.toLowerCase().contains("axe"));

        // Drop the Axe
        response = sendCommandToServer("simon: drop axe");
        assertTrue(response.toLowerCase().contains("you dropped a axe"));

        // Verify the Axe is no longer in inventory but is in the location
        response = sendCommandToServer("simon: inventory");
        assertFalse(response.toLowerCase().contains("axe"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("axe"));

        // Goto another location and verify transition
        response = sendCommandToServer("simon: goto forest");
        assertTrue(response.toLowerCase().contains("forest"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("tree"));
        assertTrue(response.toLowerCase().contains("key"));

        // Return to the cabin
        response = sendCommandToServer("simon: goto cabin");
        assertTrue(response.toLowerCase().contains("cabin"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"));
    }

    @Test
    void testInvalidCommand() {
        String response = sendCommandToServer("simon: quiet");
        response = response.toLowerCase();
        assertTrue(response.toLowerCase().contains("error"));
    }

    // Add more unit tests or integration tests here.
//    @Test
//    void testAddingBasicEntitiesToGameMap() throws Exception {
//        ArrayList<Location>gameMap = server.GameEngine.map;
//        assertTrue(gameMap.isEmpty());
//        gameMap.add(new Location("forest", "dark scary woodlands area"));
//        assertTrue(gameMap.size() == 1);
//        gameMap.add(new Location("cabin", "cosy area"));
//        assertTrue(gameMap.size() == 2);
//        gameMap.add(new Location("castle", "boogy area"));
//        assertTrue(gameMap.size() == 3);
//        assertEquals(3, server.getMapSize());
//        //Now get name,desc,type of different entities to locations (extra work?)
//    }
//
//    @Test
//    void testBasicAddNewPlayerToGameMap() throws Exception {
//        ArrayList<Location>gameMap = server.map;
//        gameMap.add(new Location("forest", "dark scary woodlands area"));
//        gameMap.add(new Location("cabin", "cosy area"));
//        Player playerOne = new Player("simon", gameMap.get(1));
//        assertEquals("cabin",playerOne.getPlayerCurrentLocation().getName());
//        playerOne = new Player("simon", gameMap.get(0));
//        assertEquals("forest",playerOne.getPlayerCurrentLocation().getName());
//    }


    @Test
    void testAdvancedActions() {
        String response;
        response = sendCommandToServer("mihir: inv");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: get axe");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: get potion");
        response = sendCommandToServer("mihir: look");
        //TODO: Response so get cannot be furniture
        response = sendCommandToServer("mihir: get trapdoor");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: goto forest");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: inv");
        response = sendCommandToServer("mihir: get key");
        response = sendCommandToServer("mihir: inv");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: chop axe");
        assertTrue(response.toLowerCase().contains("you cut down the tree with the axe"));
    }

    @Test
    void testMultipleUsers() {
        String responseSimon = sendCommandToServer(randomiseCasing("simon: look"));
        String responseMihir = sendCommandToServer(randomiseCasing("Mihir: look"));
        assertTrue(responseSimon.toLowerCase().contains("cabin"), "Simon should be able to see his location.");
        assertTrue(responseMihir.toLowerCase().contains("forest"), "Mihir should be able to see his location.");
    }

    @Test
    void testMultiPlayerGameStateChange() {
        sendCommandToServer("Simon: pick up key");
        String response = sendCommandToServer("Mihir: inventory");
        assertFalse(response.toLowerCase().contains("key"), "Mihir should not have the key picked up by Simon.");
    }

    @Test
    void decorativeCommandTestOne() {
        sendCommandToServer("Simon: please get the potion");
        String response = sendCommandToServer("Mihir: inventory");
        assertFalse(response.toLowerCase().contains("key"), "Mihir should not have the key picked up by Simon.");
    }

    //Currently: Monday13thMay: 38Passed/53 -> 15 Failures
    //Due next Monday therefore AIM: Wednesday complete TODO find out testing days -> less that 10 test failures then submit after he runs last test (ie no changes after)
    //TODO: 1. Read through docs carefullyY! Pick out any features I've missed! Write down features below.

    //Advanced:
    //TODO: 2. Make my own entity/action files!!!:
        // TODO: Handling things like producing in currently location logic

    //Before Submitting:
    //TODO: Code cleanup - find out approx lines of code + try match
    //TODO: Code Quality - following last feedback
    //TODO: 100% code coverage testing

    /*[TESTS]:
        T2: Game Engine:
            Server always running, however: "When a client connects to the server, the server accepts the connection and assigns a unique identifier to the client to distinguish it from other clients."
            ie for every new player -> assigns a unique identifier to the client? Does this mean new port?
            [Test Multiplayer] -> is game state (GameEngine) reset with each new command? TODO: testing inventory game state, run in multiple server, ie simon in one, mihir in another, test that the command dependency order is the same
            [Test Multiplayer] -> is game players reset?
                [Test server] -> only config is loaded into GameServer EACH TIME server is restarted, thats all! //TODO test this happens!
                [Test server] -> TODO arg length, null commands, decorative, if command is repeated, if command has username again?
            [Test client: Username] -> what happens when no username is sent as an argument, what is an appropriate username that wont cause conflicts (ie maybe no punctuation, what if its a game action?
            [Test server: Command] -> decorative commands
            [Test client/server] -> server should remain operational (ie test for handling Game Error), however should GameClient? TODO: surround client in try/catch!
            [Test client]: -> flood server with high volume of commands in short period to test high-load situation //TODO is there a delay?
            [Test client] -> TODO run multiple clients ESSENTIAL!!! threading?
            [Test server]: TODO disconnect client, is server still running?
            TODO file handling

T4 Game Entities:
- locations can only be rooms,environment that exist (ie in .dot file)
- artefacts: only thing that can be collected into player inv
- furniture - cannot be collected by player; part of location
- characters - cannot be collected by player; part of .dot
- player - user in the game


Locations:
- paths to other locations (what if no path?)/possible for paths to be one-way
- characters, artefacts, furniture in a location

T5 loading Entities:
- using jpgd parser?
- extracting graphviz objects? -> DS?
- using abstract class?
- Entities CANNOT contain SPACES!
- all entities need a name and description!
- no duplicate entities! door + door does not work, however door + trapdoor works!
- starting point is correct?
- entities that have no location being stored in storeroom?

ASSUME entity files are in VALID form!


T6: Game Actions
- no you may assume???

- test at least ONE trigger phrase (initiating the action)
- test at least ONE subject entities 
	- are ALL subjects available in:
		i. player.getInventory().contain(subject_entity_needed))
		ii. available in CURRENT.LOCATION?
	- when acted does produce store artefact in player.inventory, however char,furniture,location does not work?

- (opt)consumed enties - eaten up by action
- (opt)produced enties - generated by action
- AT LEAST ONE narration for EACH action

- each trigger keyphrase are NOT unique (probs missing!!!)
	- ie OPEN door <- can be one action
	- OPEN backdoor <- could be another action

- trigger phrase CANNOT contain names of entities (lock lock with key)
	- WHY is this a challenge???

- I/O (eg get key door open)
	- GET requires AT LEAST one subject entity
	- OPEN says that it needs entity door + key entity 
		- entity door is not Artefact type, therefore must be in location
		- entity door is Artefact, check player Inv FIRST, THEN c.location
	- If true -> produce/consume; else gameerror


Produce:
- moves game.entity FROM ITS position in MAP (inc storeroom) -> c.location entity_list
	Methods:
	- findGameEntityPosition, if found return string location; if false GameError
	- placeIntoCurrentLocation -> C.Location to update TYPE_LIST -> ENTITY_LIST (NEVER playerInv) -> return narration

Consume:
- moves game.entity FROM ITS position in MAP (inc storeroom) -> storeroom
- if CO-LOCATION: - moves game.entity FROM ITS position in MAP (inc storeroom) -> c.location entity_list HOWEVER if game.entity in c.location + is Subject
 - ie open lock with key -> NO CO-LOCATION means once key is used it is moved to storeroom, no longer able to be used
- CO-LOCATION ON COULD means if key is required again???  

	Methods:
	- findGameEntityPosition, if found return string location; if false GameError
	- placeIntoCurrentLocation -> C.Location to update TYPE_LIST -> ENTITY_LIST (NEVER playerInv) -> return narration

if game.entity in another players inventory -> out of bounds

as unlock key produces location -> consume location should REMOVE path! TEST!!



T7: Loading Actions:
- using JAXP?
- using DocumentBuilder? -> DS same used?

Data Struct:
- are duplicate actions being handled?
	- ie if open door == swing door, are duplicate removed?
	- if open door and open backdoor (2 different actions) BUT currently have backdoor as subject valid -> is action triggered?

ACTION FILES ARE VALID!!!



T8: Command Flexibility:

1. Case insensitive
	- is door && DOOR in same game? -> GameError
	- input -> case insensitive -> output (lowercase)

2. Decorated Commands
	- chop tree with axe; please chop the tree with axe -> additional words == TRUE

3. Words Ordering
	- chop tree with axe == use axe to chop tree TRUE

4. Partial Commands:
	- unlock trapdoor with key == TRUE (if both trigger words!) ie handle AT LEAST ONE TRIGGER WORD, ignore the rest
	
5. EXTRANEOUS Entities:
	- AT LEAST ONE TRIGGER PHRASE + ONE SUBJECT! 
	- ^ ignored for look, health, inv, inventory
	- open potion with hammer == false! 2 subjects input - ONLY 1 needed!
	- get key from forest == false! 2 subjects input - ONLY 1 needed!

therefore look get == true; look get forest == false (NO subjects needed!)
1. check if basic command (inv/health/look) 
	- from command are there any subjects, if yes return false!
2. get/drop - if subjects > 1:
		return false
3. custom action - if subject > REQUIRED_SUBJECT_COUNT:
		return false

!!!
6. Ambiguous Command:
	- look goto forest; look is valid, goto forest is valid, return false!
	- open door trapdoor (2 different actions with door in one subject, trapdoor in the other)
		- return there is more than one 'open' action possible - which one do you want to perform ?

7. Composite Command:
	- look + goto forest SHOULD NOT BE SUPPORTED! ie perform look first, then goto -> return error! 

8. GameError
	- using inv and look to test for assertTrue/assertFalse
	- different GameError for different situations?



    */


}

