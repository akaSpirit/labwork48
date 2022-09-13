package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FileService {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path candidatesPath = Paths.get("data/candidates.json");
    private static final Path userPath = Paths.get("data/users.json");

    public static List<Candidate> readCandidates() {
        var type = new TypeToken<List<Candidate>>() {}.getType();
        String json = "";
        try {
            json = Files.readString(candidatesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return GSON.fromJson(json, type);
    }

    public static void writeCandidates(List<Candidate> candidates) {
        String json = GSON.toJson(candidates);
        try {
            byte[] arr = json.getBytes();
            Files.write(candidatesPath, arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, User> readUsers() {
        var type = new TypeToken<Map<String, User>>() {}.getType();
        String json = "";
        try {
            json = Files.readString(userPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return GSON.fromJson(json, type);
    }

    public static void writeUsers(Map<String, User> users) {
        String json = GSON.toJson(users);
        try {
            byte[] arr = json.getBytes();
            Files.write(userPath, arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
