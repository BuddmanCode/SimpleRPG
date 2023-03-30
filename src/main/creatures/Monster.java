package main.creatures;

import main.items.Money;

import java.util.NoSuchElementException;
//Обычный монстр. Должен иметь при себе ресурсы для обогащения героя. Хотя бы деньги
public class Monster extends Creature {
    public Monster(CreatureStatPack stats, int level) throws NoSuchElementException {
        super(stats, level);
        //Добавить рандомную генерацию лута от уровня. (а потом и от типа)
        super.putNumerousItem(new Money(), (Math.max((level - 4), 1)) + rand.nextInt(10));
    }
}
