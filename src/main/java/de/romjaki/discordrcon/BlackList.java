package de.romjaki.discordrcon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlackList /* Pretty racist, huh? */ {
    private static File saveFile;

    static {
        saveFile = new File("discord-blacklist.txt");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PublicAPI
    public static void removeBlacklistUser(String discordId) {
        transmute(blacklist -> blacklist.remove(discordId));
    }

    @PublicAPI
    public static void addBlacklistUser(String discordId) {
        transmute(blacklist -> blacklist.add(discordId));
    }

    @PublicAPI
    public static void transmute(Consumer<Set<String>> function) {
        Set<String> blacklistUsers = getBlacklistUsers();
        function.accept(blacklistUsers);
        saveBlacklistUsers(blacklistUsers);
    }

    @PublicAPI
    public static Set<String> getBlacklistUsers() {
        try (Scanner scanner = new Scanner(saveFile).useDelimiter("\\a")) {
            return Stream
                    .of(scanner
                            .next()
                            .split("\n"))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    @PublicAPI
    public static void saveBlacklistUsers(Set<String> ids) {
        try (FileWriter writer = new FileWriter(saveFile)) {
            writer.write(ids.stream()
                    .collect(Collectors.joining("\n")));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
