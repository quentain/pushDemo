package ser.ds.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * NetTool:��װһ�����?0%��׿�ͻ�����������˽���?
 * 
 * @author ���� ������Ϣ������߰�?
 */
public class NetTool {
	private static final int TIMEOUT = 10000;// 10��
	public static String sendTxt(String urlPath, String txt, String encoding)
			throws Exception {
		byte[] sendData=null;
		if(!txt.equals(""))
		sendData = txt.getBytes();
		URL url = new URL(urlPath);
		StringBuffer sb = new StringBuffer();

		String retData = null;
		String responseData = "";
		try{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(TIMEOUT);
//		// ���ͨ��post�ύ��ݣ����������������������
//		conn.setDoOutput(true);
//		conn.setRequestProperty("Content-Type", "text/json");
//		conn.setRequestProperty("Charset", encoding);
//		if(!txt.equals(""))
//		conn.setRequestProperty("Content-Length", String
//				.valueOf(sendData.length));		
//		OutputStream outStream = conn.getOutputStream();
//		if(!txt.equals(""))
//		outStream.write(sendData);
//		outStream.flush();
//		outStream.close();
		

		if (conn.getResponseCode() == 200) {
		
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is,"UTF-8");
			int len = 0;
			char[] buf = new char[1024];

			while ((len = isr.read(buf)) != -1)
			{
				sb.append(new String(buf, 0, len));
			}

			is.close();
			isr.close();
			Util.NetState = 1;

		}
		}catch(Exception e)
		{
			String msg=e.getMessage();
		}		
		finally{
			return sb.toString();
		}
		
	}
	private void getImage(String url,String path,String name) throws MalformedURLException {
		
		Bitmap bitmap;
	    URL myFileUrl;
			myFileUrl = new URL(url);
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(new PatchInputStream(is));
				savePictureToLocal(bitmap, path, name);
				is.close();
				Util.NetState = 1;
			} catch (IOException e) {
				e.printStackTrace();
			}
		

	}
	public class PatchInputStream extends FilterInputStream {

		protected PatchInputStream(InputStream in) {
			super(in);
		}

		@Override
		public long skip(long n) throws IOException {
			long m = 0l;
			while (m < n) {
				long _m = in.skip(n - m);
				if (_m == 0l) {
					break;
				}
				m += _m;
			}
			return m;
		}

	}
	/**
	 * 将图片保存在本地
	 * */
	private void savePictureToLocal(Bitmap bitmap, String path, String name) {
		String pictureName = path + name;
		File file = new File(path);
		if (!file.exists()) {//在创建之前首先要删除之前的残存的数据
			file.mkdir();
		}
		FileOutputStream out;
		try {
			file = new File(pictureName);
			if(file.exists())
			{
				file.delete();
			}
			file.createNewFile();
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			System.out.println("保存图片");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * �ϴ��ļ�
	 */
	public static String sendFile(String urlPath, String filePath,
			String newName) throws Exception {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		URL url = new URL(urlPath);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		/* ����Input��Output����ʹ��Cache */
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		/* ���ô��͵�method=POST */
		con.setRequestMethod("POST");
		/* setRequestProperty */

		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);
		/* ����DataOutputStream */
		DataOutputStream ds = new DataOutputStream(con.getOutputStream());
		ds.writeBytes(twoHyphens + boundary + end);
		ds.writeBytes("Content-Disposition: form-data; "
				+ "name=\"file1\";filename=\"" + newName + "\"" + end);
		ds.writeBytes(end);

		/* ȡ���ļ���FileInputStream */
		FileInputStream fStream = new FileInputStream(filePath);
		/* ����ÿ��д��1024bytes */
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int length = -1;
		/* ���ļ���ȡ�����������?*/
		while ((length = fStream.read(buffer)) != -1) {
			/* ������д��DataOutputStream�� */
			ds.write(buffer, 0, length);
		}
		ds.writeBytes(end);
		ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

		/* close streams */
		fStream.close();
		ds.flush();

		/* ȡ��Response���� */
		InputStream is = con.getInputStream();
		int ch;
		StringBuffer b = new StringBuffer();
		while ((ch = is.read()) != -1) {
			b.append((char) ch);
		}
		/* �ر�DataOutputStream */
		ds.close();
		return b.toString();
	}

	/**
	 * ͨ��get��ʽ�ύ����������
	 */
	public static String sendGetRequest(String urlPath,
			Map<String, String> params, String encoding) throws Exception {

		// ʹ��StringBuilder����
		StringBuilder sb = new StringBuilder(urlPath);
		sb.append('?');

		// ���Map
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append('=').append(
					URLEncoder.encode(entry.getValue(), encoding)).append('&');
		}
		sb.deleteCharAt(sb.length() - 1);
		// ������
		URL url = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "text/xml");
		conn.setRequestProperty("Charset", encoding);
		conn.setConnectTimeout(TIMEOUT);
		// ���������Ӧ����?00�����ʾ�ɹ�?
		if (conn.getResponseCode() == 200) {
			// ��÷�������Ӧ�����
			BufferedReader in = new BufferedReader(new InputStreamReader(conn
					.getInputStream(), encoding));
			// ���?
			String retData = null;
			String responseData = "";
			while ((retData = in.readLine()) != null) {
				responseData += retData;
			}
			in.close();
			return responseData;
		}
		return "sendGetRequest error!";

	}

	/**
	 * ͨ��Post��ʽ�ύ����������,Ҳ������������json��xml�ļ�
	 */
	public static String sendPostRequest(String urlPath,
			Map<String, String> params, String encoding) throws Exception {
		StringBuilder sb = new StringBuilder();
		// ������Ϊ��
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				// Post��ʽ�ύ����Ļ�������ʡ�����������볤��?
				sb.append(entry.getKey()).append('=').append(
						URLEncoder.encode(entry.getValue(), encoding)).append(
						'&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		// �õ�ʵ��Ķ��������
		byte[] entitydata = sb.toString().getBytes();
		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(TIMEOUT);
		// ���ͨ��post�ύ��ݣ����������������������
		conn.setDoOutput(true);
		// ����ֻ�����������������ݳ��ȵ�ͷ�ֶ�
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// conn.setRequestProperty("Content-Type", "text/xml");
		conn.setRequestProperty("Charset", encoding);
		conn.setRequestProperty("Content-Length", String
				.valueOf(entitydata.length));
		OutputStream outStream = conn.getOutputStream();
		// ��ʵ�����д���������
		outStream.write(entitydata);
		// �ڴ��е����ˢ��?
		outStream.flush();
		outStream.close();
		// ���������Ӧ����?00�����ʾ�ɹ�?
		if (conn.getResponseCode() == 200) {
			// ��÷�������Ӧ�����
			BufferedReader in = new BufferedReader(new InputStreamReader(conn
					.getInputStream(), encoding));
			// ���?
			String retData = null;
			String responseData = "";
			while ((retData = in.readLine()) != null) {
				responseData += retData;
			}
			in.close();
			return responseData;
		}
		return "sendText error!";
	}

	/**
	 * ������HTTPS��ȫģʽ���߲���cookie��ʱ��ʹ��HTTPclient�᷽��ܶ�?ʹ��HTTPClient����Դ��Ŀ����������ύ����?
	 */
	public static String sendHttpClientPost(String urlPath,
			Map<String, String> params, String encoding) throws Exception {
		// ��Ҫ�Ѳ���ŵ�NameValuePair
		List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				paramPairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		// �����������б��룬�õ�ʵ�����?
		UrlEncodedFormEntity entitydata = new UrlEncodedFormEntity(paramPairs,
				encoding);
		// ����һ������·��
		HttpPost post = new HttpPost(urlPath);
		// ��������ʵ��
		post.setEntity(entitydata);
		// ���������?
		DefaultHttpClient client = new DefaultHttpClient();
		// ִ��post����
		HttpResponse response = client.execute(post);
		// ��״̬���л�ȡ״̬�룬�ж���Ӧ���Ƿ���Ҫ��
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, encoding);
			BufferedReader reader = new BufferedReader(inputStreamReader);// ���ַ��õġ�
			String s;
			String responseData = "";
			while (((s = reader.readLine()) != null)) {
				responseData += s;
			}
			reader.close();// �ر�������
			return responseData;
		}
		return "sendHttpClientPost error!";
	}

	/**
	 * ���URLֱ�Ӷ��ļ����ݣ�ǰ��������ļ����е��������ı�������ķ���ֵ�����ļ����е�����
	 */
	public static String readTxtFile(String urlStr, String encoding)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			// ����һ��URL����
			URL url = new URL(urlStr);
			// ����һ��Http����
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			// ʹ��IO����ȡ���?
			buffer = new BufferedReader(new InputStreamReader(urlConn
					.getInputStream(), encoding));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 */
	public static int downloadFile(String url, String path, String fileName)
			throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = getInputStreamFromUrl(url);
			File resultFile = write2SDFromInput(path, fileName, inputStream);
			if (resultFile == null) {
				return -1;
			}

		} catch (Exception e) {
			return -1;
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				throw e;
			}
		}
		return 0;
	}

	/**
	 * 
	 * 
	 * @param urlStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException {
		URL url = new URL(urlStr);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlConn.getInputStream();
		return inputStream;
	}

	/**
	 * ��һ��InputStream��������д�뵽SD����
	 */
	private static File write2SDFromInput(String directory, String fileName,
			InputStream input) {
		File file = null;
		String SDPATH = Environment.getExternalStorageDirectory().toString();
		FileOutputStream output = null;
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdir();
		}
		try {
			file = new File(dir +"/"+fileName);
			if(file.exists())
				file.delete();//清楚之前的数�?
			file.createNewFile();
			output = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	  /* 上传文件至Server的方�?*/
    public static void uploadFile(String pUrl,String pService,String path,String name)
    {
      String end = "\r\n";
      String twoHyphens = "--";
      String boundary = "*****";
      
      try
      {
        URL url =new URL(pUrl+pService);
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        /* 允许Input、Output，不使用Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        /* 设置传�?的method=POST */
        con.setRequestMethod("POST");
        /* setRequestProperty */
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
        
        	
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
  	   
        /* 设置DataOutputStream */
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());	        
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; " +
                      "name=\"file1\";filename=\"" +
                      name +"\"" + end);
        ds.writeBytes(end);  
    
        /* 取得文件的FileInputStream */
        FileInputStream fStream = new FileInputStream(path);
        /* 设置每次写入1024bytes */
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int length = -1;
        /* 从文件读取数据至缓冲�?*/
        while((length = fStream.read(buffer)) != -1)
        {
          /* 将资料写入DataOutputStream�?*/
          ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

        /* close streams */
        fStream.close();
	        
     
        ds.flush();
        /* 取得Response内容 */
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b =new StringBuffer();
        while( ( ch = is.read() ) != -1 )
        {
          b.append( (char)ch );
        }
        if(!b.equals(""))//上传有结果返�?
        {
      	  //转化为相应的json，然后取出相应的message的�?输出即可
      	  JSONObject jsonObj =new JSONObject(b.toString());//���Ƚ������ת��?
      	  if(jsonObj.has("message"))
      	  {
      		  //ParamUtil.CurrentMessage=jsonObj.getString("message");
      		Util.CurrentMessage="上传成功";
      	  }
        }
        ds.close();
      }
      catch(Exception e)//直接进入到异常界�?
      {
        String msg=e.getMessage();
        Util.CurrentMessage="资源上传失败";
      }
    }
}
