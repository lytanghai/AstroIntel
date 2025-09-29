package com.tanghai.announcement.utilz;

import java.util.Set;

public class Commander {

    private static final Set<String> COMMANDS =
            Set.of(
                    "/start",
                    "/ping",
                    ""
            );

    public static boolean isValid(String command) {
        return COMMANDS.contains(command);
    }

}
