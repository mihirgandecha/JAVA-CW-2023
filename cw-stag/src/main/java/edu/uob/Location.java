package edu.uob;

import java.util.ArrayList;
import java.io.File;
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

  }

  public void getArtefact(Artefact artefact) {

  }


  public void addCharacters(Character character) {

  }

  public void getCharacters(Character character) {

  }

  public void addFurniture(Furniture furniture) {

  }

  public void getFurniture(Furniture furniture) {

  }
}
