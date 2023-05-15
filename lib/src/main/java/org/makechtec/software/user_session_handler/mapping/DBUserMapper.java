package org.makechtec.software.user_session_handler.mapping;

import org.makechtec.software.caltentli.provider.User;
import org.makechtec.software.caltentli.provider.UserProvider;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.query_process.QueryCaller;
import org.makechtec.software.sql_support.query_process.statement.ParamType;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.util.HashMap;
import java.util.Optional;

public class DBUserMapper implements UserProvider {

    private final ConnectionInformation connection;

    public DBUserMapper(ConnectionInformation connection) {
        this.connection = connection;
    }

    @Override
    public Optional<User> byUsername(String username) {

        var statement =
                StatementInformation.builder()
                        .setPrepared(true)
                        .setQueryString("CALL user_by_username(?)")
                        .addParamAtPosition(1, username, ParamType.TYPE_STRING)
                        .build();


        var caller = new QueryCaller(connection, statement);

        var resultMap = new HashMap<String,String>();

        caller.call( resultSet -> {
            resultSet.next();

            resultMap.put("username", resultSet.getString("username"));
            resultMap.put("hashedPassword", resultSet.getString("password"));

        });

        var user = new User(resultMap.get("username"), resultMap.get("hashedPassword"));

        return Optional.of(user);
    }
}
