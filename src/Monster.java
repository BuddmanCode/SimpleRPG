import java.util.NoSuchElementException;

public class Monster extends Creature {
    public Monster(CreatureStatPack stats, int level) throws NoSuchElementException {
        super(stats, level);
        //Добавить рандомную генерацию лута от уровня. (а потом и от типа)
        super.putNumerousItem(new Money(), (Math.max((level - 4), 1)) + rand.nextInt(10));
    }
}
