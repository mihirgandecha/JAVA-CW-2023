package edu.uob;

import java.util.ArrayList;
import java.util.List;

/*
    Represents a location with a name, description, and a collection of entities within it.
    Entities include: Artefact, Character, and Furniture.
 */

public class Location extends GameEntity {
  public String location;
  public String description;
  public List<Artefact> artefacts;
  public List<Character> characters;
  public List<Furniture> furnitures;
  public ArrayList<String> pathTo;

  public Location(String name, String description) throws Exception {
    //saving locations itself as Game Entity
    super(name, description, GameEntityType.LOCATION);
    this.location = name;
    this.description = description;
    this.artefacts = new ArrayList<>();
    this.characters = new ArrayList<>();
    this.furnitures = new ArrayList<>();
    this.pathTo = new ArrayList<>();
  }

  public void addArtefact(Artefact artefact) {
    this.artefacts.add(artefact);
  }

  public List<Artefact> getArtefact(Artefact artefact) {
    return this.artefacts;
  }

  public String getArtefactsToString() {
    if(this.artefacts.isEmpty()) {
      return "No artefacts found";
    }
    return this.artefacts.toString();
  }

  public void addCharacters(Character character) {
    this.characters.add(character);
  }

  public List<Character> getCharacters(Character character) {
    return this.characters;
  }

  public String getCharactersToString() {
    if(this.characters.isEmpty()) {
      return "No characters found";
    }
    return this.furnitures.toString();
  }

  public void addFurniture(Furniture furniture) {
    this.furnitures.add(furniture);
  }

  public List<Furniture> getFurniture(Furniture furniture) {
    return this.furnitures;
  }

  public String getFurnitureToString() {
    if(this.furnitures.isEmpty()) {
      return "No furnitures found";
    }
    return this.furnitures.toString();
  }

}
