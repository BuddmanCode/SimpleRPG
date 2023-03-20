import java.util.Optional;

public interface Usable {
    public <T extends Human> Optional<Item> use(T target);
}
