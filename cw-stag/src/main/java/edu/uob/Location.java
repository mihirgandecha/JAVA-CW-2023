package edu.uob;

import java.util.ArrayList;
import java.util.List;

/*
    Represents a location with a name, description, and a collection of entities within it.
    Entities include: Artefact, Character, and Furniture.
 */

public class Location extends GameEntity {
  private List<Artefact> artefacts;
  private List<Character> characters;
  private List<Furniture> furnitures;

  public Location(String name, String description) throws Exception {
    //saving locations itself as Game Entity
    super(name, description);
    this.artefacts = new ArrayList<>();
    this.characters = new ArrayList<>();
    this.furnitures = new ArrayList<>();
  }

  public void addArtefact(Artefact artefact) {
    this.artefacts.add(artefact);
  }

  public List<Artefact> getArtefact(Artefact artefact) {
    return this.artefacts;
  }

  public void addCharacters(Character character) {
    this.characters.add(character);
  }

  public List<Character> getCharacters(Character character) {
    return this.characters;
  }

  public void addFurniture(Furniture furniture) {
    this.furnitures.add(furniture);
  }

  public List<Furniture> getFurniture(Furniture furniture) {
    return this.furnitures;
  }
}
