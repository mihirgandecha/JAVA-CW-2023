package edu.uob;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DBServerTest {
    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        server.updateSorageFolderPath("dbtest");
    }

    @AfterEach
    public void cleanUp() throws IOException {
        if (Files.exists(Path.of(("dbtest")))){
            Files.delete(Path.of("dbtest"));
        }
    }

    @Test
    void testUpdateStorageFolderPath() {
        String basePath = System.getProperty("user.dir");
        System.out.println(basePath);
        server.updateSorageFolderPath("dbtestfolder");
        assertEquals(basePath + "/dbtestfolder", server.storageFolderPath);
    }

    @Test
    void testAbsentDirectory() throws IOException {
        server.updateSorageFolderPath("dbtest");
        Path testAbsDir = Path.of(server.storageFolderPath);
        if (Files.exists(testAbsDir)) {
            Files.delete(testAbsDir);
        }
        server.createDirectoryIfAbsent();
        assertTrue(Files.exists(testAbsDir));
    }

    @Test
    void testExistingDirectory() {
        server.updateSorageFolderPath("databases");
        Path testAbsDir = Path.of(server.storageFolderPath);
        server.createDirectoryIfAbsent();
        assertTrue(Files.exists(testAbsDir));

    }

    //test for File.seperator
    public class FilePathConverter {
        public String convertToWindowsPath(String unixPath) {
            return unixPath.replace('/', '\\');
        }
    }

    @Test
    void testWindowsAbsPath() {
        FilePathConverter converter = new FilePathConverter();
        converter.convertToWindowsPath(server.storageFolderPath);
        System.out.println(server.storageFolderPath);
        server.convertToPlatformIndependant(server.storageFolderPath);
        String basePath = System.getProperty("user.dir");
        assertEquals(basePath + "/dbtest", server.storageFolderPath);
    }

// name tests: cannot have :, what if space? what is valid name for dir?
    // test for branch cov + add to CI/DC
    //copy table
}
