package main;

import main.db.DBConnection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DBConnection.Conn();
        UserInterfaceConsole interfaceConsole = new UserInterfaceConsole();
        interfaceConsole.start();
        DBConnection.CloseDB();
    }
}