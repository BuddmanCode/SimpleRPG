package main.creatures;

import java.util.NoSuchElementException;

//Должен предоставлять интерфейс гостиницы/постоялого двора. На данный момент только обозначает такую возможность, отдых вызывается напрямую
public class Hotelier extends Human {

    public Hotelier(CreatureStatPack stats, int level, String name) throws NoSuchElementException {
        super(stats, level, name);
    }
    //может предоставлять услуги
    @Override
    public boolean isServiceable() { //можно торговать
        return isAlive() && !isAgressive();
    }
}
