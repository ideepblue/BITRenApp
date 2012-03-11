package org.bitren.app.customview;

import org.bitren.app.AboutActivity;
import org.bitren.app.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class MenuDialog {

	Context mContext;
	Dialog mDialog;
	
	public MenuDialog(Context context) {
		mContext = context;
	}
	
	public void setDialog(Dialog dialog) {
		mDialog = dialog;
	}
	
	public View getView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View layoutMenu = inflater.inflate(R.layout.layout_menu_dialog, null);
		layoutMenu.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		RelativeLayout relativeLayoutAbout;
		RelativeLayout relativeLayoutExit;
		
		relativeLayoutAbout = (RelativeLayout) layoutMenu.findViewById(R.id.relativeLayout_Menu_About);
		relativeLayoutAbout.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
			    		intent.setClass(mContext, AboutActivity.class);
			    		mContext.startActivity(intent);					
			    		mDialog.dismiss();
					}
				}
			);
		
		relativeLayoutExit = (RelativeLayout) layoutMenu.findViewById(R.id.relativeLayout_Menu_Exit);
		relativeLayoutExit.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
			    		System.exit(0);			
					}
				}
			);
		return layoutMenu;
	}
}
