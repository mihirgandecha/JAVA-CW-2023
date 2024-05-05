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

  public boolean getEntityForProduce(String item) {
    if(this.entityList == null){
      setAllEntities();
    }
    for (GameEntity entity : entityList) {
      if (entity.getName().equalsIgnoreCase(item)) {
        return true;
      }
    }
    return false;
  }

  public GameEntity setEntityForProduce(String item) {
    for (GameEntity entity : entityList) {
      if (entity.getName().equalsIgnoreCase(item)) {
        entityList.remove(entity);
        return entity;
      }
    }
    return null;
  }

}
