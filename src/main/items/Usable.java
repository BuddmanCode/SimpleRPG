package main.items;

import main.creatures.Human;

import java.util.Optional;
//Предмет можно использовать на человека
public interface Usable {
    public <T extends Human> Optional<Item> use(T target);
}
