package edu.uob.DBDataHandling;
import java.io.*;

public class DBFileDirectory {
/*
DBStructure: .tab file (TEST: could be empty, or may need to generate new), or to read from provided .tab file.

DBStructure and file management:
1. `DBServer`method `storageFolderPath` -> '/databases'.
    Use File.separator

2. method: readData(filePath)
   1. Use Java File IO API to open the file from filePath for reading.
   2. Read each line from the file until no more lines are available.
   3. Split each line on tab character ("\t") to get an array of values representing a row/record.
   4. Print out each record to the console.

4. Define a method ‘createDatabase’ which accepts a databaseName as an argument.
   1. Convert the databaseName to lowercase.
   2. Create a new directory at path 'storageFolderPath/databaseName'. Ensure to use File.separator in path.

5. Define a method ‘createTable’ which accepts databaseName, tableName, and an array of columnNames as arguments.
   1. Convert the databaseName and tableName to lowercase.
   2. Create a new file at path 'storageFolderPath/databaseName/tableName'. Ensure to use File.separator in path.
   3. Write columnNames to the file in a single line separated by a tab character.

6. Define a method 'insertRecord' that accepts databaseName, tableName, and an array of values as arguments.
   1. Convert the databaseName and tableName to lowercase.
   2. Generate a unique id for each new record.
   3. Append this new record (id followed by the values separated by tab characters) to the end of respective table file.


Parsing:


*/


}
