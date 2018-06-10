package de.romjaki.discordrcon;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.io.IOException;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Util {
    private Util() {

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
