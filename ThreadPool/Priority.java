package ThreadPool;

public enum Priority{
    HIGH(10),
    MEDIUM(5),
    LOW(1);

    private int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}