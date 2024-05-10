package edu.uob;

import java.util.ArrayList;
import java.util.List;

/*
    Represents a location with a name, description, and a collection of entities within it.
    Entities include: Artefact, Character, and Furniture.
 */

public class Location extends GameEntity {
  public String locationName;
  public String description;
  public List<Artefact> artefacts;
  public List<Character> characters;
  public List<Furniture> furnitures;
  public ArrayList<String> pathTo;
  private List<GameEntity> entityList;

  public Location(String name, String description) {
    //saving locations itself as Game Entity
    super(name, description, GameEntityType.LOCATION);
    this.locationName = name;
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

  public boolean removeArtefact(String entityName) {
    return this.artefacts.removeIf(artefact -> artefact.getName().equalsIgnoreCase(entityName));
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

  public boolean removeCharacter(String entityName) {
    return characters.removeIf(character -> character.getName().equalsIgnoreCase(entityName));
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

  public boolean removeFurniture(String entityName) {
    return furnitures.removeIf(furniture -> furniture.getName().equalsIgnoreCase(entityName));
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
    this.setEntityList(new ArrayList<>());
    this.getEntityList().addAll(this.artefacts);
    this.getEntityList().addAll(this.characters);
    this.getEntityList().addAll(this.furnitures);
//    this.entityList.addAll(this.furnitures);
//    this.entityList.addAll(this.artefacts);
//    this.entityList.addAll(this.characters);
  }

  private String addPrefixIfNeeded(String description) {
    if(!description.startsWith("a ") && !description.startsWith("A ")){
      return "A " + description;
    }
    return description;
  }


  public String getAllEntitiesToString(){
    return getArtefactsToString() + getCharactersToString() + getFurnitureToString();
  }

  public void removeEntity(String entityName) {
    boolean removedFromArtefacts = removeArtefact(entityName);
    boolean removedFromCharacters = removeCharacter(entityName);
    boolean removedFromFurniture = removeFurniture(entityName);

    // Remove the entity from the main entity list only if it's removed from one of the specialized lists
    if (removedFromArtefacts || removedFromCharacters || removedFromFurniture) {
      getEntityList().removeIf(entity -> entity.getName().equalsIgnoreCase(entityName));
    }
  }

  public boolean getEntityForProduce(String item) {
    if(this.getEntityList() == null){
      setAllEntities();
    }
    for (GameEntity entity : getEntityList()) {
      if (entity.getName().equalsIgnoreCase(item)) {
        return true;
      }
    }
    return false;
  }

  public GameEntity getEntity(String entityName) {
    for (GameEntity entity : getEntityList()) {
      if (entity.getName().equalsIgnoreCase(entityName)) {
        removeEntity(entityName);
        return entity;
      }
    }
    return null;
  }


  public GameEntity setEntityForProduce(String item) {
    for (GameEntity entity : getEntityList()) {
      if (entity.getName().equalsIgnoreCase(item)) {
        getEntityList().remove(entity);
        return entity;
      }
    }
    return null;
  }

  public List<GameEntity> getEntityList() {
    return entityList;
  }

  public void setEntityList(List<GameEntity> entityList) {
    this.entityList = entityList;
  }
}
