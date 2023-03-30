package main.creatures;//import java.util.Objects;

import java.util.Objects;

//заотовка статов существа и их скейла
public final class CreatureStatPack {
    //название вида существа
    public final String type;
    //группа существа, его базовое отношение к герою
    public CreatureGroup group;
    public final int healthBase;
    public final int powerBase;
    public final int agilityBase;
    public final float healthScale;
    public final float powerScale;
    public final float agilityScale;

    public CreatureStatPack(String type, CreatureGroup group, int healthBase, int powerBase, int agilityBase, float healthScale, float powerScale, float agilityScale) throws NullPointerException {
        if(type == null) throw new NullPointerException("java.Creature type is null");
        this.type = type;
        this.group = group;
        this.healthBase = healthBase;
        this.powerBase = powerBase;
        this.agilityBase = agilityBase;
        this.healthScale = healthScale;
        this.powerScale = powerScale;
        this.agilityScale = agilityScale;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreatureStatPack)) return false;
        CreatureStatPack that = (CreatureStatPack) o;
        return type.equals(that.type);
    }
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
