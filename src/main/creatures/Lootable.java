package main.creatures;

import main.items.Item;

import java.util.Map;
//это существо можно грабить. Интерфейс "выворачивает" рюкзак.
@FunctionalInterface
public interface Lootable {
    public Map<Item, Integer> provideBackpack();
}
