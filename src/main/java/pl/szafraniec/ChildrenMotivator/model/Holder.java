package pl.szafraniec.ChildrenMotivator.model;

public class Holder<T> {
    private T object;

    private Holder(T object) {
        this.object = object;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }

    public static <R> Holder<R> of(R object) {
        return new Holder<>(object);
    }
}
