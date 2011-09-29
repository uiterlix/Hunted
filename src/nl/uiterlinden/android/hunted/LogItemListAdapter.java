package nl.uiterlinden.android.hunted;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.widget.SimpleCursorAdapter;

public class LogItemListAdapter extends SimpleCursorAdapter {

	private final LayoutInflater inflater;

	public LogItemListAdapter(LayoutInflater inflater, Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.inflater = inflater;
		setViewBinder(new LogItemViewBinder());
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View v = null;
//		LogItem item = (LogItem) getItem(position);
//		
//		v = inflater.inflate(R.layout.logitem, null);
//		TextView textView = (TextView) ((LinearLayout)v).getChildAt(0);
//		textView.setText(item.getMessage());
//		return v;
//	}

}
