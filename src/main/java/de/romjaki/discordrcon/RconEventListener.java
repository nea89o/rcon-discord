package de.romjaki.discordrcon;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static de.romjaki.discordrcon.Util.sendEmbed;

public class RconEventListener extends ListenerAdapter {
    @PublicAPI
    public static Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("adduser", new AddUserCommand());
        commands.put("queryuser", new QueryUser());
        commands.put("removeuser", new RemoveUserCommand());
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Ready");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String content = event.getMessage().getContentRaw();
        if (content.length() == 0) {
            return;
        }
        String[] arr = content.toLowerCase().trim().split("\\s+", 2);
        String commandName = arr[0].substring(1);
        if (!commands.keySet().contains(commandName)) {
            return;
        }
        if (arr.length < 2) {
            sendEmbed(event.getChannel(), "Missing an argument",
                    String.format("Usage: %s <user>", arr[0]), Color.RED, event.getAuthor());
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

        if (command.requiresAdminOrSelfInvite() && !(Util.isUserAdmin(event.getAuthor()) || Config.selfInvite)) {
            Util.sendPermissionMessage(event.getChannel(), event.getAuthor());
            return;
        }

        command.execute(event, arr);
    }

}
