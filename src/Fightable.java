//пожет наносить урон в интерфейи Damageable
@FunctionalInterface
public interface Fightable {
    public int attack(Damageable target);
}
