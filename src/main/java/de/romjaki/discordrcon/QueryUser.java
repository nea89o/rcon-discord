package de.romjaki.discordrcon;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class QueryUser implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        List<User> mentionedUsers = event.getMessage().getMentionedUsers();
        if (mentionedUsers.size() != 1) {
            Util.sendEmbed(event.getChannel(), "Please (only) mention one Person",
                    "We will then find out which Minecraft account they have registered.",
                    Color.RED, event.getAuthor());
            return;
        }
        User user = mentionedUsers.get(0);
        if (!UserMapping.hasMinecraftUserAsociated(user)) {
            Util.sendEmbed(event.getChannel(), "This user doesn't have an associated minecraft account.",
                    "", Color.RED, user);
            return;
        }
        Util.sendEmbed(event.getChannel(), "Minecraft account found!",
                String.format("%s has registered the account `%s` as his own." +
                                "\n\nThis is NOT necessarily the REAL account of %1$s. This is only what he told me.",
                        user.getAsMention(), UserMapping.getMinecraftUser(user)), Color.GREEN, user);
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
