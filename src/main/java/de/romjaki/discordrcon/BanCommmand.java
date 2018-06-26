package de.romjaki.discordrcon;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BanCommmand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        List<Member> users = event.getMessage().getMentionedMembers();
        Guild guild = event.getGuild();
        if (users.size() < 1) {
            Util.sendEmbed(event.getChannel(), "Syntax Error",
                    "Mention one or more users to ban", Color.RED, event.getAuthor());
            return;
        }
        GuildController controller = guild.getController();
        Role bannedRole = guild.getRoleById(Config.bannedRole);
        users.stream()
                .map(user -> controller.addSingleRoleToMember(user, bannedRole))
                .forEach(RestAction::queue);
        Util.sendEmbed(event.getChannel(), "Banned " + users.size() + " users", "Banned " +
                users.stream()
                        .map(IMentionable::getAsMention)
                        .collect(Collectors.joining(", ")), Color.GREEN, event.getAuthor());
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
