package net.crowmaster.esmfamil.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.crowmaster.esmfamil.R;


public class ProgressiveToast {
	static AlertDialog.Builder ToasticalertBuilder;
	static AlertDialog ToasticalertDialog;
//	Activity mActivity;
//	public ProgressiveToast(Activity a){
//		mActivity=a;
//	}
	public static void show(Activity a, String message){
		LayoutInflater inflater = (LayoutInflater) a.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast, null);
        /*ProgressBarCircularIndetermininate pb= (ProgressBarCircularIndetermininate)layout.findViewById(R.id.progressBar);
        pb.setIndeterminate(true);
        pb.getIndeterminateDrawable().setColorFilter(0xFF99CC00,
                android.graphics.PorterDuff.Mode.MULTIPLY);*/
        TextView mTextView=(TextView) layout.findViewById(R.id.customProgeressiveToast);
        String toBeShown = (message == null)?"Please be patient while SSN is interacting with server..." : message;
        mTextView.setText(toBeShown);
    	ToasticalertBuilder= new AlertDialog.Builder(a);
    	ToasticalertBuilder.setCancelable(false);
        ToasticalertDialog=ToasticalertBuilder.setView(layout).create();
        ToasticalertDialog.getWindow().
                setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ToasticalertDialog.show();
	}
	public static void dismiss(){
		if(ToasticalertDialog!=null)ToasticalertDialog.dismiss();
	}
		
}
