import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Terrain {
    private final String name;
    private final int vision;
    private final boolean passable;
    private ArrayList<CreatureStatPack> creatureStatsList; //Заменить на ссылку на статы
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
