package edu.uob;

public abstract class GameEntity
{
    private String name;
    private String description;
    private GameEntityType type;

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

    public GameEntityType getType()
    {
        return type;
    }
    @Override
    public String toString() {
        return "GameEntity{name='" + name + "', description='" + description + "', type=" + type + "}";
    }
}
