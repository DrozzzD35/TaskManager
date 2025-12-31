package server.KVServer;

import java.io.IOException;

public class KVServerLauncher {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }

}
