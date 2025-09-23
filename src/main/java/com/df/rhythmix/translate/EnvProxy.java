package com.df.rhythmix.translate;

import com.df.rhythmix.config.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * <p>EnvProxy class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
@Getter
@Setter
public class EnvProxy{


    private HashMap<String, Object> env = new HashMap<>();

    /**
     * <p>put.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     */
    public void put(String key, Object value) {
        this.env.put(key +Config.SPLIT_SYMBOL+ Config.VAR_COUNTER.get(), value);
    }

    /**
     * <p>rawPut.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     */
    public void rawPut(String key, Object value) {
        this.env.put(key, value);
    }

    /**
     * <p>rawPutAll.</p>
     *
     * @param env a {@link java.util.HashMap} object.
     */
    public  void rawPutAll(HashMap<String,Object> env){
        this.env.putAll(env);
    }



    /**
     * <p>get.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object get(String key) {
        return this.env.get(key + Config.VAR_COUNTER.get());
    }

    /**
     * <p>rawGet.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object rawGet(String key) {
        return this.env.get(key);
    }

    /**
     * <p>containsKey.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean containsKey(String key){
        return !this.env.containsKey(key);
    }

}
