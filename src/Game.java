import javax.swing.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class Game extends Thread{
    World world;
    boolean gameEnded;

    public Game() {
        try{
            world = new World();
            if (!world.heroExist()) throw new Exception("Hero not exists");
            //hero = new Hero("Hero",1,"НеВан Панчманович",0);
        } catch (Exception e) { //SQLException e) {
            throw new RuntimeException(e);
        }
        boolean gameEnded = false;
    }
    public void menu() {

        //Описание текущего места
        //для каждой стороны Описание + опциональный интерфейс (наверное)


        /*
        * 1) меню и описание ячейки
        * 2) если бой, то меню боя
        *   а) проверка побега в меню боя
        *   б) если не бой, возможности ячейки
        * 2) если бой - возможности путешествия, как возможности побега
        * 2) если не бой - возможности путешествия*/
    }
    @Override
    public void run() {
        //А надо ли оно?
    }

    public String getDescription() {
        StringBuilder res = new StringBuilder(world.currentDescription());
        res.append("\nСлева ");
        res.append(world.leftDescription());
        res.append("\nСправа ");
        res.append(world.rightDescription());
        res.append("\nСпереди ");
        res.append(world.rightDescription());
        res.append("\nСзади ");
        res.append(world.rightDescription());
        return res.toString();
    }
    /*
    * Отвечает за взаимодействие героя с окружением, предоставленным локацией, исходя из возможностей этого окружения.
    * Предоставляет набор возможностей для пользовательского интерфейса.*/
}
