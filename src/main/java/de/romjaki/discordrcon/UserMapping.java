package de.romjaki.discordrcon;

import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserMapping {
    private static final File mappingFile = new File("users.json");
    private static Map<String, String> discordIdToMinecraftUserName = new HashMap<>();

    static {
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(UserMapping::save));
    }

    private UserMapping() {

    }

    private static void save() {
        JSONObject object = new JSONObject();
        discordIdToMinecraftUserName.forEach(object::put);
        String json = object.toString();
        try (PrintWriter writer = new PrintWriter(mappingFile)) {
            writer.append(json);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to save users.json DUMP: ");
            System.err.println();
            System.err.println(json);
            System.err.println();
        }
    }

    @PublicAPI
    public static String getMinecraftUser(String discordId) {
        return discordIdToMinecraftUserName.get(discordId);
    }

    @PublicAPI
    public static String getMinecraftUser(User user) {
        return getMinecraftUser(user.getId());
    }

    @PublicAPI
    public static boolean hasMinecraftUserAsociated(User user) {
        return hasMinecraftUserAsociated(user.getId());
    }

    @PublicAPI
    public static boolean hasMinecraftUserAsociated(String id) {
        return discordIdToMinecraftUserName.containsKey(id);
    }

    @PublicAPI
    public static String replaceMinecraftUserName(User user, String newUsername) {
        return replaceMinecraftUserName(user.getId(), newUsername);
    }

    @PublicAPI
    public static String replaceMinecraftUserName(String id, String newUsername) {
        String put = discordIdToMinecraftUserName.put(id, newUsername);
        save();
        return put;
    }


    private static void load() {
        try (Scanner s = new Scanner(mappingFile).useDelimiter("\\A")) {
            loads(s.next());
        } catch (FileNotFoundException e) {
            try {
                if (mappingFile.createNewFile()) {
                    System.out.println("Found no users.json file. Creating default one.");
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mappingFile))) {
                        bos.write("{}".getBytes());
                    }
                }
            } catch (IOException e1) {
                System.err.println("Failed to create user mapping file. Check that you have write permissions for the current directory.");
                System.exit(1);
            }
        }
    }

    private static void loads(String json) {
        JSONObject object = new JSONObject(json);
        for (String id : object.keySet()) {
            discordIdToMinecraftUserName.put(id, object.getString(id));
        }
    }

    @PublicAPI
    public static boolean isInUse(String account) {
        return discordIdToMinecraftUserName.containsValue(account);
    }

    @PublicAPI
    public static void unbindAll(String name) {
        for (String id : discordIdToMinecraftUserName.keySet()) {
            if (name.equals(discordIdToMinecraftUserName.get(id))) {
                discordIdToMinecraftUserName.remove(id);
            }
        }
    }

    public static List<String> getIdsByAccount(String account) {
        return discordIdToMinecraftUserName.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), account))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static String removeUserName(User user) {
        return removeUserName(user.getId());
    }

    private static String removeUserName(String id) {
        return discordIdToMinecraftUserName.remove(id);
    }
}
