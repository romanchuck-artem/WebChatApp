package bsu.famcs.chat.util;

import bsu.famcs.chat.model.Message;
import bsu.famcs.chat.model.MessageStorage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

public final class MessageUtil {

    public static final String TOKEN = "token";
    public static final String MESSAGES = "messages";
    private static final String TN = "TN";
    private static final String EN = "EN";
    private static final String USER_NAME = "nick";
    private static final String MSG_TEXT = "msg";
    private static final String NOT_CHANGED = "not changed";
    private static final String DELETED = "isDeleted";
    private static final String ID = "id";

    private MessageUtil() {
    }

    public static String getToken(int index) {
        Integer number = index;
        return TN + number + EN;
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)));
    }

    private static String generateId() {
        return String.valueOf(MessageStorage.getSize());
    }

    public static String generateCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        return dateFormat.format(new Date());
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json) {
        String userName = (String)json.get(USER_NAME);
        String msgText = ((String)json.get(MSG_TEXT)).trim();
        if (userName != null && msgText != null) {
            Message msg =  new Message(generateId(), userName, msgText, generateCurrentDate(), NOT_CHANGED, false);
            return msg;
        }
        return null;
    }

    public static Message jsonToCurrentMessage(JSONObject json) {
        String id = (String)json.get(ID);
        String messageText = ((String)json.get(MSG_TEXT)).trim();
        String changeDate = (String)json.get(DELETED);
        if (changeDate == null) {
            changeDate = NOT_CHANGED;
        }
        if (id != null) {
            return new Message(id, null, messageText, null, changeDate, false);
        }
        return null;
    }
}