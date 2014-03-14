package dk.itu.spct.f2014.ma01.pmor.janv.androidapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	/**
	 * Listener to be invoked when button is in "Start tracking" mode.
	 */
	private final View.OnClickListener btnOnStartListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO outline:
			// 1) begin monitoring position using GPS.
			// 2) change button functionality to "Stop tracking"
			Button btnOnStart = (Button) v;
			btnOnStart.setText(R.string.lbl_btn_stop);
			// Change button behavior to "stop tracking"
			btnOnStart.setOnClickListener(MainActivity.this.btnOnStopListener);
		}
	};

	/**
	 * Listener to be invoked when button is in "Stop tracking" mode.
	 */
	private final View.OnClickListener btnOnStopListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO outline:
			// 1) stop monitoring position using GPS.
			// 2) send stop message to TriggerServer.
			// 3) Change button functionality to "Start tracking"
			Button btnOnStart = (Button) v;
			btnOnStart.setText(R.string.lbl_btn_start);
			// Change button behavior to "start tracking"
			btnOnStart.setOnClickListener(MainActivity.this.btnOnStartListener);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btnStartStop = (Button) this
				.findViewById(R.id.btnStartStopTracking);
		btnStartStop.setOnClickListener(this.btnOnStartListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
