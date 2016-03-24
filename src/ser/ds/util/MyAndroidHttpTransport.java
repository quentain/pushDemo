package ser.ds.util;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

public class MyAndroidHttpTransport extends HttpTransportSE {

	private int mTimeOut = 10000; // Ĭ�ϳ�ʱʱ��Ϊ10s

	public MyAndroidHttpTransport(String pUrl) {
		super(pUrl);
	}

	public MyAndroidHttpTransport(String pUrl, int pTimeOut) {
		super(pUrl);
		this.mTimeOut = pTimeOut;
	}

	protected ServiceConnection getServiceConnection(String pUrl)
			throws IOException {
		ServiceConnectionSE serviceConnection = new ServiceConnectionSE(pUrl);
		serviceConnection.setConnectionTimeOut(mTimeOut);
		return serviceConnection;
	}
}