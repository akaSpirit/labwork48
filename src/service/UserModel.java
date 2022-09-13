package service;

import java.util.List;
import java.util.Map;

public class UserModel {
    private final Map<String, User> users;

    public UserModel() {
        users = FileService.readUsers();
    }

    public Map<String, User> getUsers() {
        return users;
    }
}
