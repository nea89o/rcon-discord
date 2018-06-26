package de.romjaki.discordrcon;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.kronos.rkon.core.Rcon;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Util {
    private static final String USER_AGENT = "Mozilla/5.0";

    private Util() {

    }

    public static String sendGET(String url) throws IOException {
        return sendRequest("GET", url);
    }

    public static String sendPOST(String url) throws IOException {
        return sendRequest("POST", url);
    }

    private static String sendRequest(String method, String url) throws IOException {

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


        try (Scanner sc = new Scanner(con.getInputStream()).useDelimiter("\\A")) {
            return sc.next();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public static void sendEmbed(MessageChannel channel, String title, String description, Color color, User user) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(color)
                        .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                        .setTitle(title)
                        .setDescription(description)
                        .build())
                .queue(msg -> msg.delete().queueAfter(10, SECONDS));
    }

    @PublicAPI
    public static void sendPermissionMessage(MessageChannel channel, User author) {
        sendEmbed(channel, "You are lacking permissions",
                "You need special roles to perform that command.", Color.RED, author);
    }

    @PublicAPI
    public static boolean isUserAdmin(User user) {
        return Config.admins.contains(user.getId());
    }

    @PublicAPI
    public static void whitelist(String action, String name) throws IOException {
        UUID uuid = UserMapping.resolveMinecraftUser(name);
        try {
            Rcon rcon = new Rcon(Config.host, Config.port, Config.password.getBytes());
            rcon.command(String.format("whitelist %s %s", action, name));
            rcon.command("whitelist reload");
            if (action.equals("remove")) {
                rcon.command("kick " + name + " " + Config.kickMessage);
            }
        } catch (Exception e) {
            System.err.println("[RCON] Connection failed! We will manually " + action + " the user " + name);
            JSONArray arr = new JSONArray(new Scanner(Config.whitelistFile.toFile()).useDelimiter("\\A").next());
            for (int i = arr.length() - 1; i >= 0; i--) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("uuid").equalsIgnoreCase(uuid.toString())) {
                    arr.remove(i);
                }
            }
            if (action.equals("add")) {
                JSONObject userEntry = new JSONObject();
                userEntry.put("name", name);
                userEntry.put("uuid", uuid.toString());
                arr.put(userEntry);
            }
            try (FileWriter writer = new FileWriter(Config.whitelistFile.toFile())) {
                writer.write(arr.toString());
            }
        }
    }

    @PublicAPI
    public static void showIOErrorMessage(MessageChannel channel, User author) {
        Util.sendEmbed(channel, "Unknown Network error occured", "Check your console.",
                Color.RED, author);
    }

    @PublicAPI
    public static boolean testUserRoles(Member member) {
        return Config.selfInviteRoles.stream()
                .map(role -> member
                        .getRoles()
                        .stream()
                        .map(ISnowflake::getId)
                        .collect(Collectors.toList())
                        .contains(role)
                )
                .reduce(Boolean::logicalOr).orElse(true)
                &&
                !Config.bannedRoles.stream()
                        .map(role -> member
                                .getRoles()
                                .stream()
                                .map(ISnowflake::getId)
                                .collect(Collectors.toList())
                                .contains(role)
                        ).reduce(Boolean::logicalOr).orElse(false);
    }
}
