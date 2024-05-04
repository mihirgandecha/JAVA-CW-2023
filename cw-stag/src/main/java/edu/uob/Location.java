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
  public List<GameEntity> entityList;

  public Location(String name, String description) {
    //saving locations itself as Game Entity
    super(name, description, GameEntityType.LOCATION);
    this.location = name;
    this.description = description;
    this.artefacts = new ArrayList<>();
    this.characters = new ArrayList<>();
    this.furnitures = new ArrayList<>();
    this.pathTo = new ArrayList<>();
    setAllEntities();
  }

  public void addArtefact(Artefact artefact) {
    this.artefacts.add(artefact);
  }

  public List<Artefact> getArtefact(Artefact artefact) {
    return this.artefacts;
  }

  public String getArtefactsToString() {
    if(this.artefacts.isEmpty()) {
      return "";
    }
    StringBuilder s = new StringBuilder();
    for(Artefact artefact: this.artefacts) {
      String modifiedDescription = addPrefixIfNeeded(artefact.getDescription());
      s.append(modifiedDescription).append("\n");
    }
    return s.toString();
  }

  public void addCharacters(Character character) {
    this.characters.add(character);
  }

  public List<Character> getCharacters(Character character) {
    return this.characters;
  }

  public String getCharactersToString() {
    if(this.characters.isEmpty()) {
      return "";
    }
    StringBuilder s = new StringBuilder();
    for(Character character: this.characters) {
      String modifiedDescription = addPrefixIfNeeded(character.getDescription());
      s.append(modifiedDescription + "\n");
    }
    return s.toString();
  }

  public void addFurniture(Furniture furniture) {
    this.furnitures.add(furniture);
  }

  public List<Furniture> getFurniture(Furniture furniture) {
    return this.furnitures;
  }

  public String getFurnitureToString() {
    if(this.furnitures.isEmpty()) {
      return "";
    }
    StringBuilder s = new StringBuilder();
    for(Furniture furniture : this.furnitures) {
      String modifiedDescription = addPrefixIfNeeded(furniture.getDescription());
      s.append(modifiedDescription + "\n");
    }
    return s.toString();
  }

  public void setAllEntities() {
    this.entityList = new ArrayList<>();
    this.entityList.addAll(this.artefacts);
    this.entityList.addAll(this.characters);
    this.entityList.addAll(this.furnitures);
  }

  private String addPrefixIfNeeded(String description) {
    if(!description.startsWith("A ")){
      return "A " + description;
    }
    return description;
  }

  public String getAllEntitiesToString(){
    return getArtefactsToString() + getCharactersToString() + getFurnitureToString();
  }

  public void removeEntity(String entityName){
    for(int i=0; i<entityList.size(); i++){
      if(entityList.get(i).getName().equalsIgnoreCase(entityName)){
        entityList.remove(i);
      }
    }
  }

//  public int getEntityIndexByType(GameEntityType entityType, String entityName) throws Exception {
//    List<? extends GameEntity> targetList;
//    switch (entityType) {
//      case ARTEFACT:
//        targetList = this.artefacts;
//        break;
//      case CHARACTER:
//        targetList = this.characters;
//        break;
//      case FURNITURE:
//        targetList = this.furnitures;
//        break;
//      default:
//        throw new Exception("Unsupported entity type: " + entityType);
//    }
//    for (int i = 0; i < targetList.size(); i++) {
//      if (targetList.get(i).getName().equalsIgnoreCase(entityName)) {
//        return i;
//      }
//    }
//    throw new GameError("Entity not found: " + entityName + " of type " + entityType);
//  }



}
