package main.creatures;

//Группы существ . Необходимо для выбора конструктора. Думаю, отдельный класс для этого избыточен.
public class CreatureGroup {
    public final String name;
    public final int relationshipId;
    public CreatureGroup(String name, int relationshipId) {
        this.name = name;
        this.relationshipId = relationshipId;
    }
}
