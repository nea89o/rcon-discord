package de.romjaki.discordrcon;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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


        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
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
        Main.rcon.command(String.format("whitelist %s %s", action, name));
        Main.rcon.command("whitelist reload");
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
                .reduce((a, b) -> a | b).orElse(true);
    }
}
