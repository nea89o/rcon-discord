package de.romjaki.discordrcon;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.AuthenticationException;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {

    public static JDA jda;

    public static void main(String[] args) {
        try {
            new Rcon(Config.host, Config.port, Config.password.getBytes()).disconnect();
        } catch (IOException e) {
            System.err.println("RCON: Failed to connect to remote host");
        } catch (AuthenticationException e) {
            System.err.println("RCON: Failed to authentificate on remote host");
        }

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(Config.token)
                    .addEventListener(new RconEventListener())
                    .buildAsync();
        } catch (LoginException e) {
            System.err.println("Discord: Failed to login");
            System.exit(1);
        }
    }
}
