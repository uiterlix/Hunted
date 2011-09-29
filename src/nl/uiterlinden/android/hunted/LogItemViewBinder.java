package nl.uiterlinden.android.hunted;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.uiterlinden.android.hunted.domain.LogItem;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class LogItemViewBinder implements ViewBinder {
	
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	@Override
	public boolean setViewValue(View view, Cursor cursor, int position) {
		if (position == 3) {
			Date date = new Date(cursor.getLong(3));
			String formattedDate = sdf.format(date);
			((TextView)view).setText(formattedDate);
			return true;
		}
		if (position == 1) {
			Long directionAsLong = cursor.getLong(1);
			String text = null;
			if (directionAsLong == LogItem.Direction.IN.toInt()) {
				text = "<";
				((LinearLayout)view.getParent()).setBackgroundResource(R.color.darkgreen);
				((TextView)view).setTextColor(Color.YELLOW);
			} else if (directionAsLong == LogItem.Direction.OUT.toInt()) {
				text = ">";
				((LinearLayout)view.getParent()).setBackgroundResource(R.color.black);
				((TextView)view).setTextColor(Color.GREEN);
			} else {
				text = "?";
			}
			((TextView)view).setText(text);
			return true;
		}
		return false;
	}

}
