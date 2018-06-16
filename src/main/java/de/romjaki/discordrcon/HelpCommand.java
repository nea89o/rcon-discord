package de.romjaki.discordrcon;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HelpCommand implements Command {


    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        event.getChannel()
                .sendMessage(new EmbedBuilder()
                        .setColor(Color.magenta)
                        .setDescription(getTopicByName(getTopicName(args)))
                        .setFooter("This message will self destroy in 60 seconds", null)
                        .build()).queue(msg -> msg.delete().queueAfter(60, SECONDS));
    }


    private String getTopicByName(String name) {
        try (Scanner scanner = new Scanner(HelpCommand.class.getResourceAsStream(name)).useDelimiter("\\A")) {
            return scanner.next();
        } catch (NoSuchElementException e) {
            return "No help topic found.";
        }

    }

    private String getTopicName(String[] args) {
        if (args.length == 1) {
            return "/helptopics/default.txt";
        }
        if (args[1].matches("[^a-z0-9A-Z_\\-]")) {
            return "/helptopics/invalid.txt";
        }
        return "/helptopics/" + args[1] + ".txt";
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
