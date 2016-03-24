package ser.ds.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Context;

public class WebServiceSoap  {
	/**
	 * ��ȡSoapObject����Ĺ�������?ʹ��Soap����webservice.
	 * 
	 * */
	private static final String NAMESPACE = "http://logic.services.wegis.supermap.com/"; // 公司那边的服�?
	// WebService��ַ
	private static String URL = "";
	public WebServiceSoap(Context context) {
		String server_address = Util.getServerAddress(context);
		URL = server_address;
	}
	public WebServiceSoap() {
	}
	public void SetUrl(String mURL)
	{
		WebServiceSoap.URL=mURL;
	}
	/**
	 * ��ݷ�����?����ֵ������Ӧ���xml��ʽ�ļ���SoapObject����
	 * */
	public static String getSoapObject(String jsontext) {
		SoapObject request = new SoapObject(NAMESPACE, "execute");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); // 		
		envelope.dotNet = false; //不是支持.net的，而是java
		request.addProperty("arg0", jsontext);
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		int timeout = 10000; // settimeout 10s
		//String URL = "http://192.168.1.51:8080/WEGIS-00-WEB_SERVICE/WSWebService?wsdl";
		MyAndroidHttpTransport ht = new MyAndroidHttpTransport(URL, timeout);		
		ht.debug = true;
		Object result = null;
		try {			 
			ht.call(null, envelope);//	
			
			result =  envelope.getResponse();
			Util.NetState = 1;
		} catch (Exception ex) {		
			Util.NetState = 0;			
		}finally
		{
 			return String.valueOf(result);
		}
	}
	//REST Services
	public static String getSoapObjectRES(String Url) {
		HttpClient client = new DefaultHttpClient();
		
        HttpGet httpGet = new HttpGet(Url);
        HttpResponse response;
        StringBuffer buffer = new StringBuffer();
		try
		{
			response = client.execute(httpGet);
			InputStream inputStream =  response.getEntity().getContent();
	        
	        BufferedReader bufferReader = new BufferedReader(
	                new InputStreamReader(inputStream));
	        String str = new String("");
	        while ((str = bufferReader.readLine()) != null) {
	            buffer.append(str);
	        }
	        bufferReader.close();
		} catch (Exception e)		
		{
			String msg=e.getMessage();
		} 
       finally{
		//这里得到的是�?��json数据类型�?              
		return buffer.toString();
       }
	}


	
	/**
	 * ��ݷ�����?����ֵ �ϴ��ļ����ֽ����顣
	 * 
	 * @throws XmlPullParserException
	 * @throws IOException
	 * */
	public Object uploadSoapBytes(String pSERVICE_NAME,
			String pMETHOD_NAME, byte[] pBytes, String pParaName )
			throws Exception {
		MarshalBase64 marshal = new MarshalBase64();
		SoapObject request = new SoapObject(NAMESPACE, pMETHOD_NAME);	
		request.addProperty("pBytes", pBytes);
		request.addProperty("pName", pParaName);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(request);
		marshal.register(envelope);
		int timeout = 10000; 
		//String url="http://192.168.1.119/HPwebservice.asmx?op=UploadImage";
		String url=URL+"?op="+pMETHOD_NAME;
		String soapaction =NAMESPACE+"/"+pSERVICE_NAME+".asmx";
		MyAndroidHttpTransport httpTransport = new MyAndroidHttpTransport(url,
				timeout);
		httpTransport.debug = true;
		try{
		    httpTransport.call(soapaction, envelope);
		    }
			catch(Exception e)
			{
				String msg=e.getMessage();
			}
		Object response = envelope.getResponse();
		return response;
	}

}