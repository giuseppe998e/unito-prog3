package it.unito.prog.model.modules.synchronize;

public final class SynchronizeManagerFactory {
    public static <T> SynchronizeManager<T> newInstance() {
        return new SynchronizeManagerImpl<>();
    }
}
