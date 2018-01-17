package de.romjaki.discordrcon;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {
    void execute(MessageReceivedEvent event, String[] args);

    boolean requiresAdmin();

    boolean requiresAdminOrSelfInvite();
}
