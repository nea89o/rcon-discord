package de.romjaki.discordrcon;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static de.romjaki.discordrcon.UserMapping.isInUse;
import static de.romjaki.discordrcon.UserMapping.removeUserName;
import static de.romjaki.discordrcon.Util.hasBannedRole;
import static de.romjaki.discordrcon.Util.testUserRoles;

public class RconEventListener extends ListenerAdapter {
    @PublicAPI
    public static Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("link", new AddUserCommand());
        commands.put("query", new QueryUser());
        commands.put("unlink", new RemoveUserCommand());
        commands.put("querymc", new QueryMcUser());
        commands.put("help", new HelpCommand());
        commands.put("unban", new UnbanCommand());
        commands.put("ban", new BanCommmand());
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Ready as " + event.getJDA().getSelfUser().getName());
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        super.onGuildMemberLeave(event);
        String mcUser = removeUserName(event.getUser());
        tryRemoveUser(mcUser);
    }

    private void tryRemoveUser(String mcUser) {
        if (mcUser != null) {
            if (!isInUse(mcUser)) {
                try {
                    Util.whitelist("remove", mcUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
        if (BlackList.getBlacklistUsers().contains(event.getMember().getUser().getId())) {
            event.getGuild().getController().addRolesToMember(event.getMember(),
                    event.getGuild().getRoleById(Config.bannedRole)).queue();
        }
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (!testUserRoles(event.getMember())) {
            String mcUser = removeUserName(event.getUser());
            tryRemoveUser(mcUser);
        }
        if (!hasBannedRole(event.getMember())) {
            BlackList.removeBlacklistUser(event.getMember().getUser().getId());
        }
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (hasBannedRole(event.getMember())) {
            BlackList.addBlacklistUser(event.getMember().getUser().getId());
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String content = event.getMessage().getContentRaw();
        if (content.length() == 0) {
            return;
        }
        String[] arr = content.toLowerCase().trim().split("\\s+", 2);
        if (!arr[0].startsWith(Config.prefix)) {
            return;
        }
        String commandName = arr[0].substring(1);
        if (!commands.keySet().contains(commandName)) {
            return;
        }
        Command command = commands.get(commandName);
        if (command == null) {
            return;
        }
        event.getMessage().delete().queue();
        if (command.requiresAdmin() && !Util.isUserAdmin(event.getMember())) {
            Util.sendPermissionMessage(event.getChannel(), event.getAuthor());
            return;
        }

        if (command.requiresAdminOrSelfInvite() && !(Util.isUserAdmin(event.getMember()) ||
                (Config.selfInvite && Util.testUserRoles(event.getMember())))) {
            Util.sendPermissionMessage(event.getChannel(), event.getAuthor());
            return;
        }
        try {
            command.execute(event, arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
