package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DBUseTest {
    public DBServer server;
    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        if(server == null){
            server = new DBServer();
        }
    }

    @AfterAll
    public static void cleanDBFolder() {
        File databaseFolder = new File(Paths.get("databases").toAbsolutePath().toString());
        File[] directories = databaseFolder.listFiles();
        if (directories == null) return;
        for (File directory : directories) {
            File[] files = directory.listFiles();
            if (files == null) continue;
            for (File file : files) file.delete();
            directory.delete();
        }
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    public static String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // Randomizes the case of characters in the input string
    public static String randomiseCasing(String inFromGenerateRandomName) {
        StringBuilder randomiseCaseForName = new StringBuilder();
        Random random = new Random();
        for (char c : inFromGenerateRandomName.toCharArray()) {
            if (random.nextBoolean()) {
                randomiseCaseForName.append(Character.toUpperCase(c));
            } else {
                randomiseCaseForName.append(Character.toLowerCase(c));
            }
        }
        return randomiseCaseForName.toString();
    }

    @Test
    public void testUseIsValid(){
        String randomName = randomiseCasing(generateRandomName());
        String query1 = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String query2 = sendCommandToServer("USE " + randomName + ";");
        assert(query2.contains("[OK]"));
    }

//    @Test
//    public void testUseCaseInsensitive(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "UsE " + randomName.toUpperCase() + ";";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//    }
//
//    @Test
//    public void testUseWithSpaces(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "        USE          " + randomName + "  ;   ";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//    }
//
//    @Test
//    public void testUseSpacesAndCase(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "  Use   " + randomName + "            ;  ";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//    }
//
//    @Test
//    public void testUseNonExistingDB(){
//        String randomName = generateRandomName();
//        String response = "  Use   " + randomName + "            ;  ";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
//
//    @Test
//    public void testMissingSemiColon(){
//        String randomName = generateRandomName();
//        String response = "  Use   " + randomName + "             ";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
//
//    @Test
//    public void testCase1(){
//        dbServer.handleCommand("CREATE DATABASE testDB;");
//        sendCommandToServer("use testDB;");
//        dbServer.handleCommand("CREATE TABLE people(Name, Age, Email);");
//        dbServer.handleCommand("CREATE TABLE sheds(Name, Height, PurchaserID);");
//        dbServer.handleCommand("insert into sheds values('Dorchester', 1800, 2);");
//        dbServer.handleCommand("insert into sheds values('Plaza', 1200, 1);");
//        dbServer.handleCommand("insert into sheds values('Excelsior', 1000, 0);");
//        dbServer.handleCommand("insert into people values('Bob', 21, 'bob@bob.net');");
//        dbServer.handleCommand("insert into people values('Harry', 32, 'harry@harry.net');");
//        dbServer.handleCommand("insert into people values('Chris', 42, 'chris@chris.net');");
//        String response = "use testDB;";
//        dbServer.handleCommand(response);
//        response = "update people set age = -21 where name == 'Bob';";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//        cleanFolder("testDB");
//    }
//
//    @Test
//    public void testCase2(){
//        sendCommandToServer("CREATE DATABASE testDB;");
//        sendCommandToServer("use testDB;");
//        sendCommandToServer("CREATE TABLE people(Name, Age, Email);");
//        sendCommandToServer("CREATE TABLE sheds(Name, Height, PurchaserID);");
//        sendCommandToServer("insert into sheds values('Dorchester', 1800, 2);");
//        sendCommandToServer("insert into sheds values('Plaza', 1200, 1);");
//        sendCommandToServer("insert into sheds values('Excelsior', 1000, 0);");
//        sendCommandToServer("insert into people values('Bob', 21, 'bob@bob.net');");
//        sendCommandToServer("insert into people values('Harry', 32, 'harry@harry.net');");
//        sendCommandToServer("insert into people values('Chris', 42, 'chris@chris.net');");
//        String response = "use testDB;";
//        sendCommandToServer(response);
//        response = "insert into people values('hello my name is space', 45, 'email@email.com');";
//        response = sendCommandToServer(response);
//        assert(response.contains("[OK]"));
//    }
}