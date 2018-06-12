package de.romjaki.discordrcon;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.stream.Collectors;

import static de.romjaki.discordrcon.Util.sendEmbed;

public class QueryMcUser implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            sendEmbed(event.getChannel(), "Missing an argument",
                    String.format("Usage: %s <user>", args[0]), Color.RED, event.getAuthor());
            return;
        }
        sendEmbed(event.getChannel(), String.format("Users with the Minecraft username %s", args[1]),
                UserMapping.getIdsByAccount(args[1])
                        .stream()
                        .map(user -> "<@" + user + ">")
                        .collect(Collectors.joining(", ")),
                Color.magenta, event.getAuthor());

    }

    @Override
    public boolean requiresAdmin() {
        return false;
    }

    @Override
    public boolean requiresAdminOrSelfInvite() {
        return false;
    }
}
