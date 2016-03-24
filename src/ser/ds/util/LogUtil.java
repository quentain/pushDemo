package ser.ds.util;

import android.util.Log;
/**
 * ���ڼ�¼��־��Ϣ��ͨ����
 * 
 * @author mayiengly
 * @date 2014-04-08
 * 
 */
public class LogUtil {
    private static final String TAG = "LogUtil";
    /**
   	 * Log��¼��Ϣ
   	 * 
   	 * @param msg 
   	 * 			 ��¼��Ϣ String
   	 * 
   	 */
    public static final void thread(String msg){
        Thread t = Thread.currentThread();
        Log.d(TAG, "<" + t.getName() + ">id: " + t.getId() + ", Priority: " + t.getPriority() + ", Group: " + t.getThreadGroup().getName()
                + (msg != null? ",Msg:" + msg : ""));
    }
    /**
	 * Log��¼��Ϣ
	 * 
	 * @param msg 
	 * 			��¼��Ϣ String 
	 * 
	 */
    public static final void d(String msg)
    {
        Log.d(TAG, msg);
    }
}