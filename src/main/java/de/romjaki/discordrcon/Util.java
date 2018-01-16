package de.romjaki.discordrcon;

import net.dv8tion.jda.core.entities.User;

import java.io.IOException;

public class Util {
    private Util() {

    }

    public static boolean isUserAdmin(User user) {
        return Config.admins.contains(user.getId());
    }

    public static void whitelistPlayer(String name) throws IOException {
        whitelist("add", name);
    }

    public static void whitelist(String action, String name) throws IOException {
        Main.rcon.command(String.format("whitelist %s %s", action, name));
        Main.rcon.command("whitelist reload");
    }
}
