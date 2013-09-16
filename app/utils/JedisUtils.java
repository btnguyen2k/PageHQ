package utils;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

import com.github.ddth.plommon.utils.JsonUtils;
import com.typesafe.plugin.RedisPlugin;

/**
 * JRedis utility.
 * 
 * @author btnguyen
 * 
 */
public class JedisUtils {

    private final static String DEFAULT_ENCODING = "UTF-8";

    /**
     * Serializes an object.
     * 
     * @param obj
     * @return
     */
    public static byte[] serialize(Object obj) {
        String json = JsonUtils.toJsonString(obj);
        try {
            return json != null ? json.getBytes(DEFAULT_ENCODING) : null;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes an object.
     * 
     * @param data
     * @return
     */
    public static Object deserialize(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            String json = new String(data, DEFAULT_ENCODING);
            return JsonUtils.fromJsonString(json);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes an object.
     * 
     * @param data
     * @param clazz
     * @return
     */
    public static <T> T deserializes(byte[] data, Class<T> clazz) {
        if (data == null) {
            return null;
        }
        try {
            String json = new String(data, DEFAULT_ENCODING);
            return JsonUtils.fromJsonString(json, clazz);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /*------------------------------Lists and Queues------------------------------*/
    /**
     * Gets length of a list/queue.
     * 
     * @param listName
     * @return
     */
    public static Long listSize(String listName) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            return jedis.llen(listName);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * Pushes value(s) to a queue.
     * 
     * Note: this method uses command LPUSH to push values to the list specified
     * by {@code queueName}. See: {@link http://redis.io/commands/lpush}.
     * 
     * @param queueName
     * @param values
     * @return
     */
    public static Long queuePush(String queueName, byte[]... values) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            byte[] key = SafeEncoder.encode(queueName);
            return jedis.lpush(key, values);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * Pushes value(s) to a queue.
     * 
     * Note: this method uses command LPUSH to push values to the list specified
     * by {@code queueName}. See: {@link http://redis.io/commands/lpush}.
     * 
     * @param queueName
     * @param values
     * @param ttlSeconds
     * @return
     */
    public static Long queuePush(String queueName, int ttlSeconds, byte[]... values) {
        Long result = queuePush(queueName, values);
        expire(queueName, ttlSeconds);
        return result;
    }

    /**
     * Pops a value from a queue.
     * 
     * Note: this method uses command RPOP to pop value out of the list
     * specified by {@code queueName}. See {@link http://redis.io/commands/rpop}
     * . .
     * 
     * @param queueName
     * @return
     */
    public static byte[] queuePop(String queueName) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            byte[] key = SafeEncoder.encode(queueName);
            return jedis.rpop(key);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /*------------------------------Lists and Queues------------------------------*/

    /*------------------------------Sets------------------------------*/
    /**
     * See {@link http://redis.io/commands/sadd}.
     * 
     * @param setName
     * @param values
     * @return
     */
    public static Long setAdd(String setName, byte[]... values) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            byte[] key = SafeEncoder.encode(setName);
            return jedis.sadd(key, values);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * See {@link http://redis.io/commands/sadd}.
     * 
     * @param setName
     * @param ttlSeconds
     * @param values
     * @return
     */
    public static Long setAdd(String setName, int ttlSeconds, byte[]... values) {
        Long result = setAdd(setName, values);
        expire(setName, ttlSeconds);
        return result;
    }

    /**
     * See {@link http://redis.io/commands/scard}.
     * 
     * @param setName
     * @return
     */
    public static Long setSise(String setName) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            return jedis.scard(setName);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * See {@link http://redis.io/commands/sismember}.
     * 
     * @param setName
     * @param value
     * @return
     */
    public static Boolean setIsMember(String setName, byte[] value) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            byte[] key = SafeEncoder.encode(setName);
            return jedis.sismember(key, value);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * See {@link http://redis.io/commands/smembers}.
     * 
     * @param setName
     * @return
     */
    public static Set<byte[]> setMembers(String setName) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            byte[] key = SafeEncoder.encode(setName);
            return jedis.smembers(key);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * See {@link http://redis.io/commands/srandmember}.
     * 
     * @param setName
     * @return
     */
    public static byte[] setRandomMember(String setName) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            byte[] key = SafeEncoder.encode(setName);
            return jedis.srandmember(key);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * See {@link http://redis.io/commands/spop}.
     * 
     * @param setName
     * @return
     */
    public static byte[] setPop(String setName) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            byte[] key = SafeEncoder.encode(setName);
            return jedis.spop(key);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /*------------------------------Sets------------------------------*/

    /**
     * See {@link http://redis.io/commands/del}.
     * 
     * @param keys
     * @return
     */
    public static Long delete(String... keys) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            return jedis.del(keys);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * See {@link http://redis.io/commands/expire}.
     * 
     * @param key
     * @param ttlSeconds
     * @return
     */
    public static Long expire(String key, int ttlSeconds) {
        JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = pool.getResource();
        try {
            return jedis.expire(key, ttlSeconds);
        } finally {
            pool.returnResource(jedis);
        }
    }
}
