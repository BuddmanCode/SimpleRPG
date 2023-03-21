import java.util.Optional;
//Однократно используемый предмет (например, зелье)
//не стоило разделять однократно и многократно используемые предметы
public abstract class OnceUsableItem extends Item implements Usable {
    private int effectValue;
    private Item wasteItem;
    private int remainingUses=1;

    public OnceUsableItem(int itemID, String name, int cost, int level, int effectValue, boolean isSingle, Item wasteItem) {
        super(itemID, name, cost, level, isSingle);
        this.effectValue = effectValue;
        this.wasteItem = wasteItem;
    }
    public OnceUsableItem(int itemID, String name, int cost, int level, int effectValue, boolean isSingle) {
        this(itemID, name, cost, level, effectValue, isSingle, null);
    }
    public int getEffectValue() {
        return effectValue;
    }
    //эффект, оказыыаемый предметом
    abstract <T> void effect(T target) throws ClassCastException;
    //интерфейс использования. проверяет возможность использования, вызывает эффект
    @Override
    public <T extends Human> Optional<Item> use(T target){
        if(remainingUses > 0) {
            try {
                effect(target);
                remainingUses--;
            } catch (ClassCastException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        if (remainingUses<=0) {
            return Optional.ofNullable(wasteItem);
        }
        return Optional.ofNullable(this);
    }
}
