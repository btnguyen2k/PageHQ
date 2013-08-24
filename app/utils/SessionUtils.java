package utils;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.github.ddth.plommon.utils.*;

import play.libs.Json;
import play.mvc.Controller;

public class SessionUtils {
    @SuppressWarnings("unchecked")
    public static Object getSession(String key) {
        String sValue = Controller.session(key);
        if (sValue == null) {
            return null;
        }
        try {
            JsonNode jsonNode = Json.parse(sValue);
            Map<String, Object> sEntry = Json.fromJson(jsonNode, Map.class);
            Long expiry = DPathUtils.getValue(sEntry, "expiry", Long.class);
            if (expiry == null || expiry.longValue() > System.currentTimeMillis()) {
                return sEntry.get("value");
            } else {
                Controller.session().remove(key);
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void setSession(String key, Object value) {
        setSession(key, value, 0);
    }

    /**
     * Sets a session entry.
     * 
     * @param key
     * @param value
     * @param ttl
     *            time-to-live in seconds
     */
    public static void setSession(String key, Object value, long ttl) {
        Map<String, Object> sEntry = new HashMap<String, Object>();
        sEntry.put("value", value);
        if (ttl > 0) {  
            sEntry.put("expiry", System.currentTimeMillis() + ttl * 1000);
        }
        Controller.session(key, Json.toJson(sEntry).asText());
    }
}
