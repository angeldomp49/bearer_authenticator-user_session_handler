package org.makechtec.software.user_session_handler.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.makechtec.software.caltentli.provider.Session;
import org.makechtec.software.sql_support.ConnectionInformation;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class MappingTest {

    private ConnectionInformation connection;
    private DBUserMapper userMapper;
    private DBSessionMapper sessionMapper;

    @BeforeEach
    public void setUp(){
        this. connection = new ConnectionInformation(
                "makech",
                "3nitrotoluenO@",
                "localhost",
                "3306",
                "auth"
        );

        this.userMapper = new DBUserMapper(connection);
        this.sessionMapper = new DBSessionMapper(connection);
    }

    @Test
    public void testUserMapping(){


        var user = userMapper.byUsername("jhon");

        assertTrue(user.isPresent());
        assertEquals("03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", user.get().hashedPassword());
    }

    @Test
    public void testSessionMapping(){

        String token = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        var session = new Session(
                Calendar.getInstance(),
                null,
                "jhon",
                true,
                token
        );

        sessionMapper.openSession(session);

        var persistedSession = sessionMapper.byToken(token);

        assertTrue(persistedSession.isPresent());
        assertTrue(persistedSession.get().isOpen());

        sessionMapper.closeSessionByToken(token);

        var closedSession = sessionMapper.byToken(token);

        assertTrue(closedSession.isPresent());
        assertFalse(closedSession.get().isOpen());

    }

}
