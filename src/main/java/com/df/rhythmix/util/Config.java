package com.df.rhythmix.util;

import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.atomic.AtomicLong;

public class Config {


    public final static AtomicLong VAR_COUNTER = new AtomicLong(System.currentTimeMillis()+ RandomUtil.randomLong(System.currentTimeMillis()));

    public final static String SPLIT_SYMBOL = "$";

}
