package de.romjaki.discordrcon;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    void execute(GuildMessageReceivedEvent event, String[] args);

    boolean requiresAdmin();

    boolean requiresAdminOrSelfInvite();
}
