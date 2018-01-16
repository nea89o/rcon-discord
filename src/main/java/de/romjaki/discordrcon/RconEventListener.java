package de.romjaki.discordrcon;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RconEventListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        String content = event.getMessage().getContentRaw();
        String[] arr = content.toLowerCase().split("\\s+", 2);
        System.out.println(content);
        if (!(arr[0].equals("/adduser") || arr[0].equals("/removeuser"))) {
            return;
        }
        if (!Util.isUserAdmin(event.getAuthor()) && !Config.selfInvite) {
            sendPermissionMessage(event.getChannel(), event.getAuthor());
            return;
        }
        if (arr.length < 2) {
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Missing an argument")
                            .setDescription(String.format("Usage: %s <user>", arr[0]))
                            .build())
                    .queue(msg -> msg.delete().queueAfter(10, SECONDS));
            return;
        }
        if (arr[0].equals("/adduser")) {
            proccessMessage(event, "add", "added", arr[1]);
        }
        if (arr[0].equals("/removeuser")) {
            if (!Util.isUserAdmin(event.getAuthor())) {
                sendPermissionMessage(event.getChannel(), event.getAuthor());
                return;
            }
            proccessMessage(event, "remove", "removed", arr[1]);
        }

    }

    private void sendPermissionMessage(MessageChannel channel, User author) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("You are lacking permissions")
                        .setThumbnail(author.getAvatarUrl())
                        .setDescription("You need to be an admin in order to perform that command.")
                        .build())
                .queue(msg -> msg.delete().queueAfter(10, SECONDS));
    }

    private void proccessMessage(MessageReceivedEvent event, String action, String actioned, String name) {
        try {
            Util.whitelist(action, name);
        } catch (IOException e) {
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Unknown Network error occured")
                            .setDescription("Check your console.")
                            .build())
                    .queue(msg -> msg.delete().queueAfter(10, SECONDS));
            e.printStackTrace();
        }
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(String.format("Successfully %s User %s", actioned, name))
                        .build()
        ).queue(msg -> msg.delete().queueAfter(10, SECONDS));
    }
}
