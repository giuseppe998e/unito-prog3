package it.unito.prog.model;

public final class ModelFactory {
    public static Model newInstance() {
        return new ModelImpl();
    }
}
