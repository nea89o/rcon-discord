package de.romjaki.discordrcon;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static de.romjaki.discordrcon.UserMapping.isInUse;
import static de.romjaki.discordrcon.UserMapping.removeUserName;
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
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (!testUserRoles(event.getMember())) {
            String mcUser = removeUserName(event.getUser());
            tryRemoveUser(mcUser);
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

        if (command.requiresAdmin() && !Util.isUserAdmin(event.getAuthor())) {
            Util.sendPermissionMessage(event.getChannel(), event.getAuthor());
            return;
        }

        if (command.requiresAdminOrSelfInvite() && !(Util.isUserAdmin(event.getAuthor()) ||
                (Config.selfInvite && Util.testUserRoles(event.getMember())))) {
            Util.sendPermissionMessage(event.getChannel(), event.getAuthor());
            return;
        }

        command.execute(event, arr);

        event.getMessage().delete().queue();
    }

}
