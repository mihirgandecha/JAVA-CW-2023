package edu.uob;

import java.util.ArrayList;
import java.io.File;

/*
    Represents a location with a name, description, and a collection of entities within it.
    Entities include: Artefact, Character, and Furniture.
 */

public class Location {
  ArrayList<Furniture> furnitures;
  ArrayList<Character> characters;
  ArrayList<Path> paths;

  public Location (File entitiesFile, File actionsFile) throws Exception {
    furnitures = new ArrayList<>();
    characters = new ArrayList<>();
    paths = new ArrayList<>();
  }

}
