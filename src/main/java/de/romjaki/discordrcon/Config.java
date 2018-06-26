package de.romjaki.discordrcon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Config {
    public static JSONObject config;
    public static JSONObject rcon;
    public static int port;
    public static String password;
    public static String host;
    public static JSONObject discord;
    public static String token;
    public static List<String> adminRoles;
    public static boolean selfInvite;
    public static List<String> selfInviteRoles;
    public static String prefix;
    public static String welcomeMessage;
    public static String bannedRole;
    public static String kickMessage;
    public static Path whitelistFile;

    static {
        try (Scanner s = new Scanner(new File("config.json")).useDelimiter("\\A")) {
            config = new JSONObject(s.next());
        } catch (FileNotFoundException e) {
            System.out.println("config.json not found!");
        }
        rcon = config.getJSONObject("rcon");
        port = rcon.getInt("port");
        host = rcon.getString("server");
        password = rcon.getString("password");
        discord = config.getJSONObject("discord");
        token = discord.getString("token");
        prefix = discord.getString("prefix");
        JSONArray temp = discord.getJSONArray("admins");
        adminRoles = new ArrayList<>();
        welcomeMessage = discord.getString("welcomemessage");
        temp.forEach(o -> adminRoles.add(o.toString()));

        selfInvite = config.getBoolean("selfinvite");

        temp = discord.getJSONArray("selfinviteroles");
        selfInviteRoles = new ArrayList<>();
        temp.forEach(o -> selfInviteRoles.add(o.toString()));

        bannedRole = discord.getString("bannedrole");

        kickMessage = config.getString("kickmessage");

        whitelistFile = FileSystems.getDefault().getPath(config.getString("whitelistfile"));
        if (whitelistFile.toFile().isDirectory()) {
            whitelistFile = whitelistFile.resolve("whitelist.json");
        }
    }
}
