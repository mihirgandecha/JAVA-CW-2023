package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DBServerTest {
    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    @Test
    void testUpdateStorageFolderPath() {
        server.updateSorageFolderPath("dbtestfolder");
        assertEquals("/home/mihirgany/IdeaProjects/cw-db/dbtestfolder", server.storageFolderPath);
    }

//   @Test
//    void createDirectoryIfAbsent_Existing() {
//        // Prepare
//        String dirPath = "existingDir";
//        Path path = Paths.get(dirPath);
//        Files.createDirectory(path);
//
//        // Execute
//        DBServer.createDirectoryIfAbsent(dirPath);
//
//        // Assert
//        assertTrue(Files.exists(path));
//
//        // Cleanup
//        Files.delete(path);
//    }
//
//    @Test
//    void createDirectoryIfAbsent_NotExisting() {
//        // Prepare
//        String dirPath = "notExistingDir";
//        Path path = Paths.get(dirPath);
//
//        // Place code here, you must assert that the folder did not exist before running the method
//        assertFalse(Files.exists(path));
//
//        // Execute
//        DBServer.createDirectoryIfAbsent(dirPath);
//
//        // Assert
//        assertTrue(Files.exists(path));
//
//        // Cleanup
//        Files.delete(path);
//    }
}
