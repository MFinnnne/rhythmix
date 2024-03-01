package com.celi.ferrum.util;

import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Config {

    public final static String NAME_SPACE = "fe_";

    public final static AtomicLong VAR_COUNTER = new AtomicLong(System.currentTimeMillis()+ RandomUtil.randomLong(System.currentTimeMillis()));

    public final static String SPLIT_SYMBOL = "$";

}
