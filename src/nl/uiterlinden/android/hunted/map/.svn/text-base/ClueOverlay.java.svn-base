package nl.uiterlinden.android.hunted.map;

import java.util.ArrayList;
import java.util.List;

import nl.uiterlinden.android.hunted.Map;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.domain.Clue;
import nl.uiterlinden.android.hunted.service.HuntedService;
import nl.uiterlinden.android.hunted.task.GevondenTask;
import nl.uiterlinden.android.hunted.task.VertrekNaarTask;
import nl.uiterlinden.android.hunted.task.WisselTask;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ClueOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	private final Map map;
	private static final String LOG_TAG = "ClueOverlay";
	private final HuntedService huntedService;
	
	public ClueOverlay(Map map, HuntedService huntedService, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.map = map;
		this.huntedService = huntedService;
	}
	
	public static Drawable boundToCenter(Drawable d) {
		return boundCenter(d);
	}
	
	public void addOverlay(OverlayItem overlay, Context context) {
	    this.context = context;
		overlays.add(overlay);
	}
	
	public void clear() {
		overlays.clear();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  final OverlayItem overlayItem = overlays.get(index);
	  String name = overlayItem.getTitle();
	  
	  String currentTarget = map.getPointsProvider().getStatus(DataProvider.CURRENT_TARGET);
	  final Clue clue = map.getPointsProvider().getClue(name);
	  Log.d(LOG_TAG, "Tapped on clue: " + clue);
	  Clue currentTargetClue = null;
	  if (currentTarget != null) {
		  currentTargetClue = map.getPointsProvider().getClue(currentTarget);
	  }
	  
	  if (clue.getFound() != null) {
//		  handleResetFoundMenu();
		  return true;
	  }
	  if (clue.getName().equals(currentTarget)) {
		  // handleWisselNaarEindpuntMenu
		  return true;
	  }
	  
	  handleGaNaarMenu(clue, currentTargetClue);
	
	  Log.d(LOG_TAG, clue.toString());
	  

	  return true;
	}
	
	private void handleGaNaarMenu(final Clue selectedClue, final Clue currentTargetClue) {
		CharSequence[] itemseq = null;
		if (currentTargetClue == null) {
			itemseq = new CharSequence[] {"Vertrek naar " + selectedClue.getName()};
		} else {
			itemseq = new CharSequence[] { currentTargetClue.getName() + " gevonden, naar " + selectedClue.getName() , "Wissel van " + currentTargetClue.getName() + " naar " + selectedClue.getName(), "Ga naar eindpunt"};
		}
		final CharSequence[] items = itemseq;

		  AlertDialog.Builder builder = new AlertDialog.Builder(context);
		  builder.setTitle("Punt " + selectedClue.getName());
		  builder.setItems(items, new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int item) {
		    	  if (item == 0) {
		    		  if (currentTargetClue != null) {
		    			  // ga naar
		    			  handleGevondenAlert(selectedClue, currentTargetClue);
		    		  } else {
		    			  // vertrek
		    			new VertrekNaarTask(context, huntedService, selectedClue.getName()).execute(null);  
//						map.createMapOverlay();
//						map.moveMapToClue(selectedClue);		    			  
		    		  }
		    		  
		    	  } else if (item == 1) {
		    		  // wissel
		    		  new WisselTask(context, huntedService, selectedClue.getName()).execute(null);
//			          map.createMapOverlay();
//			          map.moveMapToClue(selectedClue);
		    	  } else if (item == 2) {
			          // ga naar eindpunt    		  
		    	  } else if (item == 3) {
		    		  // wissel naar eindpunt
		    	  }
		      }
		  });
		  AlertDialog alert = builder.create(); 
		  alert.show();
	}
	
	private void handleGevondenAlert(final Clue selectedClue,
			final Clue currentTargetClue) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Gevonden: " + currentTargetClue.getName());
		alert.setMessage("Woord");

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		alert.setView(input);

		alert.setPositiveButton("Versturen", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String word = input.getText().toString();
				new GevondenTask(context, huntedService, word, selectedClue.getName()).execute(null);  
//				map.createMapOverlay();
//				map.moveMapToClue(selectedClue);
				// Do something with value!
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}
	
	private static int TITLE_MARGIN = 1;
	private static float FONT_SIZE = 10f;
	
    @Override
    public void draw(android.graphics.Canvas canvas, MapView mapView,
            boolean shadow)
    {
        super.draw(canvas, mapView, shadow);
        
        Paint   mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);

        List<Clue> clues = huntedService.getDataProvider().getTrackLog();
        
        Clue prevClue = null;
        for (Clue clue : clues) {
        	if (prevClue != null) {
        		GeoPoint prevCluePoint = Map.getPointForClue(prevClue);
        		GeoPoint currentCluePoint = Map.getPointForClue(clue);
        		Point fromPoint = new Point();
        		mapView.getProjection().toPixels(prevCluePoint, fromPoint);
        		Point toPoint = new Point();
        		mapView.getProjection().toPixels(currentCluePoint, toPoint);
        		canvas.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y, mPaint);
        	}
        	
        	prevClue = clue;
        }
        
        // go through all OverlayItems and draw title for each of them
        List<OverlayItem> overlaysCopy = new ArrayList<OverlayItem>(overlays);
        for (OverlayItem item : overlaysCopy)
        {
            /* Converts latitude & longitude of this overlay item to coordinates on screen.
             * As we have called boundCenterBottom() in constructor, so these coordinates
             * will be of the bottom center position of the displayed marker.
             */
            GeoPoint point = item.getPoint();
            Point markerBottomCenterCoords = new Point();
            mapView.getProjection().toPixels(point, markerBottomCenterCoords);

            /* Find the width and height of the title*/
            TextPaint paintText = new TextPaint();
            Paint paintRect = new Paint();

            Rect rect = new Rect();
            paintText.setTextSize(FONT_SIZE);
            paintText.getTextBounds(item.getTitle(), 0, item.getTitle().length(), rect);

            rect.inset(-TITLE_MARGIN, -TITLE_MARGIN);
            rect.offsetTo(markerBottomCenterCoords.x - rect.width()/2, markerBottomCenterCoords.y - 15 - rect.height());

            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setTextSize(FONT_SIZE);
            paintText.setARGB(255, 255, 255, 255);
            paintRect.setARGB(130, 0, 0, 0);

            canvas.drawRoundRect( new RectF(rect), 2, 2, paintRect);
            canvas.drawText(item.getTitle(), rect.left + rect.width() / 2,
                    rect.bottom - TITLE_MARGIN, paintText);
            
        }
        
    }

	public void populateOverlay() {
		populate();
	}
    



}
