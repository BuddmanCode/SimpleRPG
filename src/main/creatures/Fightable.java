package main.creatures;

//пожет наносить урон в интерфейи java.Damageable
@FunctionalInterface
public interface Fightable {
    public int attack(Damageable target);
}
