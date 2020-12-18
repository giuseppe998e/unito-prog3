package it.unito.prog.server;

import it.unito.prog.Main;
import it.unito.prog.model.modules.config.ConfigManager;
import it.unito.prog.server.session.SessionFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ServerImpl extends Thread implements Server {
    private final int srvPort;
    private final int srvThreads;
    private final int srvTimeout;
    private final ExecutorService threadPool;

    private ServerSocket serverSocket;

    public ServerImpl() {
        ConfigManager configManager = Main.appModel().configManager();

        srvPort = Integer.parseInt(configManager.read("srv.port"));
        srvTimeout = Integer.parseInt(configManager.read("srv.timeout"));
        srvThreads = Integer.parseInt(configManager.read("srv.threads"));

        threadPool = Executors.newFixedThreadPool(srvThreads);
    }

    @Override
    public void start() {
        Main.printLn("Starting server on port " + srvPort + "...");
        super.start();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(srvPort);
            Main.printLn("Server online! Listening for requests...");

            while (!Thread.interrupted()) {
                Socket connection = serverSocket.accept();
                connection.setSoTimeout(srvTimeout);
                threadPool.execute(SessionFactory.newInstance(connection));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Main.printLn("Server exception: " + e.getLocalizedMessage());
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
            threadPool.shutdown();
            int awaitTime = Math.min(srvThreads * 2, 300);
            if (!threadPool.awaitTermination(awaitTime, TimeUnit.SECONDS)) { // Wait 2sec for thread, max 5min
                threadPool.shutdownNow();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            Main.printLn("SocketServer exception on closing: " + e.getLocalizedMessage());
        }
        Main.printLn("Server stopped!");
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (!serverSocket.isClosed()) close();
    }
}
