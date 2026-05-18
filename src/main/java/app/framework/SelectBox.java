package app.framework;

public class SelectBox {

    private String value;
    private String name;

    private SelectBox() {}

    public String getValue() { return value; }
    public String getName()  { return name; }

    // Enforces value → name → build order at compile time
    public static ValueStep builder() {
        return new Builder();
    }

    public interface ValueStep {
        NameStep value(String value);
    }

    public interface NameStep {
        BuildStep name(String name);
    }

    public interface BuildStep {
        SelectBox build();
    }

    private static class Builder implements ValueStep, NameStep, BuildStep {

        private final SelectBox selectBox = new SelectBox();

        @Override
        public NameStep value(String value) {
            selectBox.value = value;
            return this;
        }

        @Override
        public BuildStep name(String name) {
            selectBox.name = name;
            return this;
        }

        @Override
        public SelectBox build() {
            return selectBox;
        }
    }
}