import java.util.Map;

@FunctionalInterface
public interface Lootable {
    public Map<Item, Integer> provideBackpack();
}
