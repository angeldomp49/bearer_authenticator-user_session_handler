package org.makechtec.software.user_session_handler.hashing;

import com.google.common.hash.Hashing;
import org.makechtec.software.caltentli.hashing.HashStrategy;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class SHAMask implements HashStrategy {

    @Override
    public String make(String s) {
        return
                Hashing.sha256()
                        .hashString(s, StandardCharsets.UTF_8)
                        .toString();
    }

    @Override
    public String generate() {
        return
                Hashing.sha256()
                        .hashString(Calendar.getInstance().toString(), StandardCharsets.UTF_8)
                        .toString();
    }
}
