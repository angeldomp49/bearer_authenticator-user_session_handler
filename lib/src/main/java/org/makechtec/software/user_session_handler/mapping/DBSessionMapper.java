package org.makechtec.software.user_session_handler.mapping;

import org.makechtec.software.caltentli.provider.Session;
import org.makechtec.software.caltentli.provider.SessionProvider;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.query_process.QueryCaller;
import org.makechtec.software.sql_support.query_process.statement.ParamType;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class DBSessionMapper implements SessionProvider {

    private final ConnectionInformation connection;
    private final SimpleDateFormat formatter;

    public DBSessionMapper(ConnectionInformation connection) {
        this.connection = connection;
        this.formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    }

    @Override
    public void openSession(Session session) {

        String startedDate = formatter.format(session.stardedDate().getTime());

        var statement =
                StatementInformation.builder()
                        .setPrepared(true)
                        .setQueryString("CALL open_session(?,?,?,?,?)")
                        .addParamAtPosition(1, startedDate, ParamType.TYPE_STRING)
                        .addParamAtPosition(2, null, ParamType.TYPE_STRING)
                        .addParamAtPosition(3, session.username(), ParamType.TYPE_STRING)
                        .addParamAtPosition(4, 1, ParamType.TYPE_INTEGER)
                        .addParamAtPosition(5, session.token(), ParamType.TYPE_STRING)
                        .build();

        var caller = new QueryCaller(connection, statement);

        caller.callUpdate();

    }

    @Override
    public void closeSessionByToken(String token) {

        String endedDate = formatter.format(Calendar.getInstance().getTime());

        var statement =
                StatementInformation.builder()
                        .setPrepared(true)
                        .setQueryString("CALL close_session(?, ?)")
                        .addParamAtPosition(1, token, ParamType.TYPE_STRING)
                        .addParamAtPosition(2, endedDate, ParamType.TYPE_STRING)
                        .build();

        var caller = new QueryCaller(connection, statement);

        caller.callUpdate();
    }

    @Override
    public Optional<Session> byToken(String token) {

        var statement =
                StatementInformation.builder()
                        .setPrepared(true)
                        .setQueryString("CALL session_by_token(?)")
                        .addParamAtPosition(1, token, ParamType.TYPE_STRING)
                        .build();

        var caller = new QueryCaller(connection, statement);

        var resultMap = new HashMap<String,String>();

        caller.call( resultSet -> {

            resultSet.next();

            resultMap.put("start", resultSet.getString("start"));
            resultMap.put("end", resultSet.getString("end"));
            resultMap.put("username", resultSet.getString("username"));
            resultMap.put("isOpen", resultSet.getString("is_open"));
            resultMap.put("token", resultSet.getString("token"));

        });


        var startedDate = Calendar.getInstance();
        try {
            startedDate.setTime(formatter.parse(resultMap.get("start")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar endedDate = null;

        if(Objects.nonNull(resultMap.get("end"))){
            endedDate = Calendar.getInstance();

            try {
                endedDate.setTime(formatter.parse(resultMap.get("end")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        var session = new Session(
                startedDate,
                endedDate,
                resultMap.get("username"),
                resultMap.get("isOpen").equals("1"),
                resultMap.get("token")
        );

        return Optional.of(session);
    }
}
