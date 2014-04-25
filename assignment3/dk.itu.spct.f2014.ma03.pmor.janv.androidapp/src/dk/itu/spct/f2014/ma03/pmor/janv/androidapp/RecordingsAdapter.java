package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordingsAdapter extends BaseAdapter {
	private final Context c;
	public final List<Recording> recordings = new ArrayList<>();
	
	public RecordingsAdapter(Context c) {
		this.c = c;
	}
	
	@Override
	public int getCount() {
		return recordings.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView tv = new TextView(c);
		tv.setText(recordings.get(arg0).timestamp + ": " + recordings.get(arg0).type);
		return tv;
	}

}
