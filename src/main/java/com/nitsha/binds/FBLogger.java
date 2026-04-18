package com.nitsha.binds;

//? if >=1.17 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? } else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/
//? }

public class FBLogger {
    private static String prefix = "[FastBind] ";
    //? if >=1.17 {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.MOD_ID);
    //?} else {
    /*public static final Logger LOGGER = LogManager.getLogger(Main.MOD_ID);*/
    //?}

    public static void info(String msg, Object... args) {
        LOGGER.info(prefix + msg, args);
    }

    public static void warn(String msg, Object... args) {
        LOGGER.warn(prefix + msg, args);
    }

    public static void error(String msg, Object... args) {
        LOGGER.error(prefix + msg, args);
    }

    public static void debug(String msg, Object... args) {
        LOGGER.debug(prefix + msg, args);
    }
}