package com.github.andrewdev.utilities;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class HashingUtils {
    private static final Argon2 argon2 = Argon2Factory.create();

    private HashingUtils(){}

    public static String hashPassword(String password) {
        return argon2.hash(22, 65536, 1, password.toCharArray());
    }

    public static boolean passwordMatch(String hash, String password) {
        return argon2.verify(hash, password.toCharArray());
    }
}
