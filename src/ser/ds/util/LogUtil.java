package ser.ds.util;

import android.util.Log;
/**
 * 用于记录日志信息的通用类
 * 
 * @author mayiengly
 * @date 2014-04-08
 * 
 */
public class LogUtil {
    private static final String TAG = "LogUtil";
    /**
   	 * Log记录信息
   	 * 
   	 * @param msg 
   	 * 			 记录信息 String
   	 * 
   	 */
    public static final void thread(String msg){
        Thread t = Thread.currentThread();
        Log.d(TAG, "<" + t.getName() + ">id: " + t.getId() + ", Priority: " + t.getPriority() + ", Group: " + t.getThreadGroup().getName()
                + (msg != null? ",Msg:" + msg : ""));
    }
    /**
	 * Log记录信息
	 * 
	 * @param msg 
	 * 			记录信息 String 
	 * 
	 */
    public static final void d(String msg)
    {
        Log.d(TAG, msg);
    }
}