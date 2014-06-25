package com.newhere.quicklaunch;

import com.larswerkman.holocolorpicker.*;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.newhere.sidebar.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CustomizeActivity extends Activity  implements OnColorChangedListener{
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private TextView text;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customize_activity);
		setTitle("Customize Background Color");
		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
		button = (Button) findViewById(R.id.button1);
		text = (TextView) findViewById(R.id.textView1);
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOnColorChangedListener(this);
		int currentColor = Preferences.getSidebarColor(getApplicationContext());
		text.setTextColor(currentColor);
		opacityBar.setOpacity(Color.alpha(currentColor));
		picker.setOldCenterColor(currentColor);
		picker.setColor(currentColor);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				text.setTextColor(picker.getColor());
				picker.setOldCenterColor(picker.getColor());
				SharedPreferences pref = getSharedPreferences(Preferences.fileName, MODE_PRIVATE);
		 		Editor editor = pref.edit();
		 		editor.putInt(Preferences.sidebarColor, picker.getColor());
		 		editor.commit();
		 	}
		});
	}
 	@Override
	public void onColorChanged(int color) {
		//gives the color when it's changed.
 		//Toast.makeText(getApplicationContext(), "Color changed to >"+color, 2000).show();
	}
}
