package crowmaster.net.ssn.Util;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetAccess{
	ConnectivityManager cm;
	public CheckInternetAccess(ConnectivityManager conMan){
		this.cm=conMan;
	}
	public boolean isOnline()  {
	  NetworkInfo ni = cm.getActiveNetworkInfo();
	    return (ni != null && ni.isConnected());
	}

}
