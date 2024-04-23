package edu.uob;

public enum GameEntityType {
    //Locations: Rooms, environments or places that exist within the game
    LOCATION,
    //Artefacts: Physical things within the game that can be collected by the player
    ARTEFACT,
    //Furniture: Physical things that are an integral part of a location (these can NOT be collected by the player)
    FURNITURE,
    //Characters: The various creatures or people involved in game
    CHARACTER,
    //Players: A special kind of character that represents the user in the game
    PLAYER
}




