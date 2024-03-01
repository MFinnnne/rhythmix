package com.df.rhythmix.translate;

import com.df.rhythmix.util.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class EnvProxy{


    @Getter
    @Setter
    private HashMap<String, Object> env = new HashMap<>();

    public void put(String key, Object value) {
        this.env.put(key +Config.SPLIT_SYMBOL+ Config.VAR_COUNTER.get(), value);
    }

    public void rawPut(String key, Object value) {
        this.env.put(key, value);
    }

    public  void rawPutAll(HashMap<String,Object> env){
        this.env.putAll(env);;
    }



    public Object get(String key) {
        return this.env.get(key + Config.VAR_COUNTER.get());
    }

    public Object rawGet(String key) {
        return this.env.get(key);
    }

    public boolean containsKey(String key){
        return this.env.containsKey(key);
    }

}
