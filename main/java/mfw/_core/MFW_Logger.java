package mfw._core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MFW_Logger {
	 
	public static Logger logger = LogManager.getLogger("MFW");

	public static void trace(String msg) {
		MFW_Logger.logger.trace(msg);
	}
 
	public static void info(String msg) {
		MFW_Logger.logger.info(msg);
	}
 		
	public static void warn(String msg) {
		MFW_Logger.logger.warn(msg);
	}
	
	public static void debugInfo(String msg) {
		info(msg);
	}
 
}