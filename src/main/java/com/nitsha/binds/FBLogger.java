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

    public static void info(String msg) { LOGGER.info(prefix + msg); }
    public static void warn(String msg) { LOGGER.warn(prefix + msg); }
    public static void error(String msg) { LOGGER.error(prefix + msg); }
    public static void debug(String msg) { LOGGER.debug(prefix + msg); }
}