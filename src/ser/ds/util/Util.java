package ser.ds.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import com.ds.lib.AutoScrollTextView;
import com.ds.model.InfouploadModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
public class Util {
	/**
	 * WebService��������,����activity�Ĺ�������,��������.
	 * */
	public static  String StartDate="";
	public static  String EndDate="";
	public static  String Date="";
	public static  String RestaurantName="";
	public static  String GoodType="";
	//public final static String CallInfo_Contact = "CallInfo/CallInfo_Contact.xml";
	// webservice����	
	// ��������ʱ��״̬
	public static int UnTouchTime = 0;// 
	public static int VibrationNum= 0;// 
	public static final int DOWNLOAD_START = 0;// ���ʿ�ʼ
	public static final int DOWNLOAD_END = 1;// ����
	public static final int DOWNLOAD_OK = 2;// ��
	public static final int DOWNLOAD_ERROR = 3;// ����
	public static final int DOWNLOAD_OFFLINE = 4;// ����
	public static final int DOWNLOAD_DISCONNECT = 5;// δ��������	
	public static final int DOWNLOAD_twoOK = 6;// ��
	public static final int DOWNLOAD_threeOK = 7;// ��
	public static final int DOWNLOAD_fourOK = 8;// ��
	public static final int Data_Change= 9;// 
	public static final int Map_destory= 10;// 
	public static final int Map_stop= 11;// 
	public static final int Openfire_HasNewMessage= 13;
	 public static String noticecontent = "";
	 public static String noticelogcontent = "";
	//����ͼ��ı���״̬
	public static final int FirstPage=6;
	public static final int CallIndoPage=7;
	public static final int SetPage=8;
	public static final int hasItemNow=9;
	public static String mWaring_Info = "";
	// ����״̬.1ͨ��,0δ����
	public static int NetState = 0;
	public static String getWarningInfo() {
		return mWaring_Info;
	} 
	public static void SetWaringInfo(String pStrWarning) {
		mWaring_Info = pStrWarning;
		AutoScrollTextView.ReBindText("");// /
	}                                  
	
	//��̬����
	public static String CurrentPage="";
	public static String CurrentMessage="";
	public static String UserNAME="";
	public static String CurrentItem="FirstPage";
	public static int Position=0;
	public static String NOticeTitle="";
	public static String NOticeContent="";
	public static String NOticeDate="";
	public static String DeviceId="";
	public static String Result="";
	public static InfouploadModel infoupload=new InfouploadModel();
	public static String Result1="";
	public static String Result2="";
	public static String Result3="";
	public static String puserID="";
	public static String pchannelID="";
	public static String  sendtime="";
	public static String lat="";
	public static String lng="";
	
	/**
	 * ��ȡ��������ַ
	 * 
	 * @param context
	 * @return
	 */
	public static String getServerAddress(Context context) {
		SharedPreferences mySharedpreferences;
		mySharedpreferences = context.getSharedPreferences("com.defenceservice", 0);
		return mySharedpreferences.getString("server_address",
				"http://202.200.112.91:3333/UserService/");// ����
	}
	public static String getOpenfireServerAddress() {
//		final String  openfireurl="10.16.148.57";
		final String  openfireurl="219.244.92.166";
//		final String  openfireurl="61.185.242.196";
		return openfireurl;// ��ʾ�̶���openfire��ip��ַ
	}
	// ����״̬.1ͨ��,0δ����
		//public static int NetState = 0; 				
	/**
	 * ���÷�������ַ
	 *    
	 * @param context
	 * @param server_address
	 */
	public static void setServerAddress(Context context, String server_address) {
		SharedPreferences mySharedpreferences;
		mySharedpreferences = context.getSharedPreferences("com.wdgolf", 0);
		mySharedpreferences.edit().putString("server_address", server_address)
				.commit();
	}

	//�޸�����
		public final static String Changedpwd = "changedpwd";
	/**
	 * �����û���¼��
	 * 
	 * @param context
	 * @param server_address
	 */
		public static void SaveUserDataToLocal(Context pContext, String pUserName,String pdeparment,String deviceid,String sendtime) {
			SharedPreferences mySharedpreferences;
			mySharedpreferences = pContext.getSharedPreferences("com.defenceservice", 0);
			Editor editor = mySharedpreferences.edit();//��ȡ�༭��
			editor.putString("saveuserid", pUserName);
			editor.putString("savedeparment", pdeparment);
			editor.putString("savedeviceid", deviceid);
			editor.putString("sendtime", sendtime);
			editor.commit();//�ύ�޸�
		}
			
			
	//��ȡ�ֻ���豸���루���ͣ�
		public static String getDeviceType()
		{
			String type_s=android.os.Build.MODEL + ":" 
			        + android.os.Build.VERSION.RELEASE;
		
			return type_s;
		}
		
		//��ȡIP��mark��ַ
			public static String getMac()
			{
				 String mac_s= "";
			        try {
			             byte[] mac;
			             NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
			             mac = ne.getHardwareAddress();//û�л�ȡ����ȷ��mac���
			             mac_s = byte2hex(mac);
			        } catch (Exception e) {
			        }
			        mac_s=ChageUpper(mac_s);
			         return mac_s;
			}
			 public static  String byte2hex(byte[] b) {
		         StringBuffer hs = new StringBuffer(b.length);
		         String stmp = "";
		         int len = b.length;
		         for (int n = 0; n < len; n++) {
		          stmp = Integer.toHexString(b[n] & 0xFF);
		          if (stmp.length() == 1)
		           hs = hs.append("0").append(stmp);
		          else {
		           hs = hs.append(stmp);
		          }
		         }
		         return String.valueOf(hs);
		        }

			public static String getLocalIpAddress() {  
		          try {  
		              for (Enumeration<NetworkInterface> en = NetworkInterface  
		                              .getNetworkInterfaces(); en.hasMoreElements();) {  
		                          NetworkInterface intf = en.nextElement();  
		                         for (Enumeration<InetAddress> enumIpAddr = intf  
		                                  .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
		                              InetAddress inetAddress = enumIpAddr.nextElement();  
		                              if (!inetAddress.isLoopbackAddress()) {  
		                              return inetAddress.getHostAddress().toString();  
		                              }  
		                         }  
		                      }  
		                  } catch (SocketException ex) {  
		                      Log.e("WifiPreference IpAddress", ex.toString());  
		                  }  
		               return null;  
		  } 
			  private static String ChageUpper(String mac) {
				  StringBuffer sb = new StringBuffer();
				  char[] c = mac.toCharArray();
				  for(int i=0;i<c.length;i++){		
						   if(c[i]>=97){//Сд��ĸ��ֵ��
								  if(i%2==0&&i!=0)
								   {
									  sb.append(":");
								   }
								  sb.append((c[i]+"").toUpperCase());
						   }else{
							   if(i%2==0&&i!=0)
							   {
							        sb.append(":");	
							   }
							   sb.append((c[i]+""));
						   }	
				  }
				return sb.toString();
			}
		public static String getTimeNow()
		{
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sysTimeStr = format.format(new Date());
			return sysTimeStr; 
		}
}
