import java.util.NoSuchElementException;

public class Hotelier extends Human{

    public Hotelier(CreatureStatPack stats, int level, String name) throws NoSuchElementException {
        super(stats, level, name);
    }
    @Override
    public boolean isServiceable() { //можно торговать
        return isAlive() && !isAgressive();
    }
}
