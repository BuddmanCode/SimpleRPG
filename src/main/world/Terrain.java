package main.world;

import main.creatures.CreatureStatPack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Terrain {
    //Название окружения/рельефа
    private final String name;
    //дальность видимости (не реализовано)
    private final int vision;
    //возмохность ходить
    private final boolean passable;
    //"заготовки" возможных существ
    private ArrayList<CreatureStatPack> creatureStatsList;
    public Terrain(String name, int vision, boolean passable, ArrayList<CreatureStatPack> creatureStatsList) {
        this.name = name;
        this.vision = vision;
        this.passable = passable;
        this.creatureStatsList = creatureStatsList == null ? new ArrayList<CreatureStatPack>() : creatureStatsList;
    }
    public String getName() {
        return name;
    }
    public boolean isPassable() {
        return passable;
    }
    //копия списка возможных существ
    public List<CreatureStatPack> getMonsterTypeList() {
        return List.copyOf(creatureStatsList);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Terrain)) return false;
        Terrain terrain = (Terrain) o;
        return name.equals(terrain.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    @Override
    public String toString() {
        return name;
    }
    /*public int getVision() {
        return vision;
    }*/
}
