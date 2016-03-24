package ser.ds.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

import org.ksoap2.transport.ServiceConnection;

public class ServiceConnectionSE implements ServiceConnection {
	/**
	 * �ڷ���Զ�̷������ṩ�ķ���ʱ����ʱ����Ϊ������������Ƿ����������⣬�����¿ͻ��˲�һֱ������������״̬��
	 * ��ʱ����ϣ�����Կ�������ò�����Ӧ�ĳ�ʱʱ��TimeOut.
	 * 
	 * ������д�ܰ��е�ServiceConnectionSE.Java��������ó�ʱʱ��ķ�������������Ĺ�������д�����
	 * 
	 * **/
	private HttpURLConnection mConnection;

	public ServiceConnectionSE(String url) throws IOException {
		this.mConnection = ((HttpURLConnection) new URL(url).openConnection());
		this.mConnection.setUseCaches(false);
		this.mConnection.setDoOutput(true);
		this.mConnection.setDoInput(true);
	}

	@Override
	public void connect() throws IOException {
		this.mConnection.connect();
	}

	@Override
	public void disconnect() {
		this.mConnection.disconnect();
	}

	@Override
	public void setRequestProperty(String string, String soapAction) {
		this.mConnection.setRequestProperty(string, soapAction);
	}

	@Override
	public void setRequestMethod(String requestMethod) throws IOException {
		this.mConnection.setRequestMethod(requestMethod);
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return this.mConnection.getOutputStream();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return this.mConnection.getInputStream();
	}

	@Override
	public InputStream getErrorStream() {
		return this.mConnection.getErrorStream();
	}

	// �������ӷ������ĳ�ʱʱ��,���뼶,��Ϊ�Լ���ӵķ���
	public void setConnectionTimeOut(int timeout) {
		this.mConnection.setConnectTimeout(timeout);
	}

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResponseCode() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List getResponseProperties() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFixedLengthStreamingMode(int arg0) {
		// TODO Auto-generated method stub

	}

}