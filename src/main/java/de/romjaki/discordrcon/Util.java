package de.romjaki.discordrcon;

import java.io.IOException;

public class Util {
    private Util() {

    }

    public static void whitelistPlayer(String name) throws IOException {
        whitelist("add", name);
    }

    public static void whitelist(String action, String name) throws IOException {
        Main.rcon.command(String.format("whitelist %s %s", action, name));
        Main.rcon.command("whitelist reload");
    }
}
