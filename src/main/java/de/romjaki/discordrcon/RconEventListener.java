package de.romjaki.discordrcon;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;

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
            Util.sendEmbed(event.getChannel(), "Missing an argument",
                    String.format("Usage: %s <user>", arr[0]), Color.RED, event.getAuthor());
            return;
        }
        if (arr[0].equals("/adduser")) {
            addUser(event, arr[1]);
        }
        if (arr[0].equals("/removeuser")) {
            if (!Util.isUserAdmin(event.getAuthor())) {
                sendPermissionMessage(event.getChannel(), event.getAuthor());
                return;
            }
            removeUser(event, arr[1]);
        }

    }

    private void sendPermissionMessage(MessageChannel channel, User author) {
        Util.sendEmbed(channel, "You are lacking permissions",
                "You need to be an admin in order to perform that command.", Color.RED, author);
    }

    private void addUser(MessageReceivedEvent event, String name) {
        if (UserMapping.hasMinecraftUserAsociated(event.getAuthor())) {
            String oldAccount = UserMapping.replaceMinecraftUserName(event.getAuthor(), name);
            if (UserMapping.isInUse(oldAccount)) {
                Util.sendEmbed(event.getChannel(), "Your old minecraft account is still in use.",
                        String.format("Your old minecraft account `%s` is still in use " +
                                "and will stay whitelisted.", oldAccount), Color.BLUE, event.getAuthor());
            } else {
                try {
                    Util.whitelist("remove", oldAccount);
                    Util.sendEmbed(event.getChannel(), "Replacing minecraft account",
                            String.format("Your old minecraft account `%s` will be removed", name),
                            Color.blue, event.getAuthor());
                } catch (IOException e) {
                    showIOErrorMessage(event.getChannel(), event.getAuthor());
                    e.printStackTrace();
                }
            }
        }
        try {
            Util.whitelist("add", name);
            Util.sendEmbed(event.getChannel(), "Your minecraft account has whitelisted",
                    String.format("Your minecraft account `%s` has been whitelisted.", name),
                    Color.GREEN, event.getAuthor());
        } catch (IOException e) {
            showIOErrorMessage(event.getChannel(), event.getAuthor());
            e.printStackTrace();
        }
    }

    private void showIOErrorMessage(MessageChannel channel, User author) {
        Util.sendEmbed(channel, "Unknown Network error occured", "Check your console.",
                Color.RED, author);
    }

    private void removeUser(MessageReceivedEvent event, String name) {
        try {
            Util.whitelist("remove", name);
        } catch (IOException e) {
            showIOErrorMessage(event.getChannel(), event.getAuthor());
            e.printStackTrace();
        }
        Util.sendEmbed(event.getChannel(), String.format("Successfully removed User %s", name), "",
                Color.GREEN, event.getAuthor());
    }
}
