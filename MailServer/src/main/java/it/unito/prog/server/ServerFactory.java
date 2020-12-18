package it.unito.prog.server;

public final class ServerFactory {
    public static Server newInstance() {
        return new ServerImpl();
    }
}
