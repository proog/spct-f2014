package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
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
		return recordings.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// reuse any existing view
		if(convertView == null) {
			// No existing view, inflate from resource.
			LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.recording_lv_item, null);
		}
		// Set the display text.
		TextView tv = (TextView) convertView.findViewById(R.id.lbl_fileName);
		tv.setText(recordings.get(position).fileName);
		
		return convertView;
	}

}
