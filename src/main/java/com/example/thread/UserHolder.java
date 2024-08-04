package com.example.thread;

import com.example.model.User;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/8/1 15:23
 */
public class UserHolder {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void saveUser(User userId) {userHolder.set(userId);}

    public static User getUser() {return userHolder.get();}

    public static void removeUser() {userHolder.remove();}
}
