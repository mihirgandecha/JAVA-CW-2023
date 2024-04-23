package edu.uob;

public abstract class GameEntity
{
    private String name;
    private String description;
    private GameEntity type;

    public GameEntity(String name, String description, GameEntityType type)
    {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
