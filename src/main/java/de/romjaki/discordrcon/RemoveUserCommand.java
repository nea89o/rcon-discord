package de.romjaki.discordrcon;

import net.dv8tion.jda.core.entities.IMentionable;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;

import static de.romjaki.discordrcon.Util.sendEmbed;

public class RemoveUserCommand implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getMessage().getMentionedUsers().forEach(user -> {
            String accountName = UserMapping.removeUserName(user);
            if (accountName == null) {
                sendEmbed(event.getChannel(), "No account bindings found.", String.format("%s has no accounts associated", user.getAsMention()), Color.RED, user);
                return;
            }
            if (!UserMapping.isInUse(accountName)) {
                try {
                    Util.whitelist("remove", accountName);
                } catch (IOException e) {
                    Util.showIOErrorMessage(event.getChannel(), event.getAuthor());
                    e.printStackTrace();
                    return;
                }
                sendEmbed(event.getChannel(), String.format("Successfully removed %ss Minecraft account %s",
                        user.getAsMention(), accountName), "", Color.GREEN, user);
            } else {
                sendEmbed(event.getChannel(), String.format("Removed account bindings from User %s to " +
                                "Minecraft account %s", user.getName() + '#' + user.getDiscriminator(), accountName),
                        String.format("Still keeping the Account whitelisted since %s still use it.",
                                UserMapping.getIdsByAccount(accountName).stream()
                                        .map(Main.jda::getUserById)
                                        .map(IMentionable::getAsMention)
                                        .reduce((s, s2) -> s + ", " + s2)
                                        .orElse("ERROR")),
                        Color.BLUE, event.getAuthor());
            }
        });
        if (event.getMessage().getMentionedUsers().size() == 0) {
            sendEmbed(event.getChannel(), "Missing argument", "Please mention which users' account " +
                    "binding you want to remove", Color.RED, event.getAuthor());
        }
    }

    @Override
    public boolean requiresAdmin() {
        return true;
    }

    @Override
    public boolean requiresAdminOrSelfInvite() {
        return false;
    }
}
