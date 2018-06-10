package de.romjaki.discordrcon;

import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static de.romjaki.discordrcon.Util.sendGET;

public class UserMapping {
    private static final File mappingFile = new File("users.json");
    private static Map<String, UUID> discordIdToMinecraftUUUID = new HashMap<>();

    static {
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(UserMapping::save));
    }

    private UserMapping() {

    }

    private static void save() {
        JSONObject object = new JSONObject();
        discordIdToMinecraftUUUID.forEach(object::put);
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
    public static String resolveUUID(UUID uuid) {
        if (uuid == null) return null;
        try {
            JSONArray obj = new JSONArray(sendGET("https://api.mojang.com/user/profiles/" + removeDashes(uuid) + "/names"));
            return obj.getJSONObject(obj.length() - 1).getString("name");
        } catch (IOException e) {
            e.printStackTrace();
            return "[IOERROR]";
        }
    }

    @PublicAPI
    private static UUID insertDashes(String uuid) {
        return UUID.fromString(uuid.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    @PublicAPI
    private static String removeDashes(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    @PublicAPI
    public static UUID resolveMinecraftUser(String username) {
        if (username == null) return null;
        try {
            JSONObject obj = new JSONObject(sendGET("https://api.mojang.com/users/profiles/minecraft/" + username));
            return insertDashes(obj.getString("id"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PublicAPI
    public static String getMinecraftUser(String discordId) {
        return resolveUUID(discordIdToMinecraftUUUID.get(discordId));
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
        return discordIdToMinecraftUUUID.containsKey(id);
    }

    @PublicAPI
    public static String replaceMinecraftUserName(User user, String newUsername) {
        return replaceMinecraftUserName(user.getId(), newUsername);
    }

    @PublicAPI
    public static String replaceMinecraftUserName(String id, String newUsername) {
        UUID put = discordIdToMinecraftUUUID.put(id, resolveMinecraftUser(newUsername));
        save();
        return resolveUUID(put);
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
            discordIdToMinecraftUUUID.put(id, UUID.fromString(object.getString(id)));
        }
    }

    @PublicAPI
    public static boolean isInUse(String account) {
        return discordIdToMinecraftUUUID.containsValue(resolveMinecraftUser(account));
    }

    @PublicAPI
    public static void unbindAll(String name) {
        UUID account = resolveMinecraftUser(name);
        if (account == null) return;
        for (String id : discordIdToMinecraftUUUID.keySet()) {
            if (account.equals(discordIdToMinecraftUUUID.get(id))) {
                discordIdToMinecraftUUUID.remove(id);
            }
        }
    }

    public static List<String> getIdsByAccount(String name) {
        UUID account = resolveMinecraftUser(name);
        return discordIdToMinecraftUUUID.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), account))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static String removeUserName(User user) {
        return removeUserName(user.getId());
    }

    private static String removeUserName(String id) {
        return resolveUUID(discordIdToMinecraftUUUID.remove(id));
    }
}
