package game.custom;

public class Object {
    private final Type type;

    public Object(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getIcon() {
        return switch (type) {
            case PLATFORM -> "data/platform-icon.png";
            case WIDE -> "data/wide-icon.png";
            case TRAMPOLINE -> "data/trampoline-icon.png";
            case SHORT -> "data/short-icon.png";
            case ROTATE -> "data/turn-icon.png";
            case VANISH -> "data/vanish-icon.png";
            default -> null;
        };
    }

    public String getImage() {
        return switch (type) {
            case PLATFORM -> "data/platform.png";
            case WIDE -> "data/wide-platform.png";
            case TRAMPOLINE -> "data/trampoline.png";
            case SHORT -> "data/short-platform.png";
            case ROTATE -> "data/turn-platform.png";
            case VANISH -> "data/vanish-platform.png";
            default -> null;
        };
    }

    enum Type {
        PLATFORM,
        WIDE,
        SHORT,
        ROTATE,
        VANISH,
        TRAMPOLINE
    }
}
