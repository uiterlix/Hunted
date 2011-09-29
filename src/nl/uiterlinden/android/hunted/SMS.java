package nl.uiterlinden.android.hunted;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import nl.uiterlinden.android.hunted.service.HuntedService;
import nl.uiterlinden.android.hunted.task.AanmeldTask;
import nl.uiterlinden.android.hunted.task.AbstractTask;
import nl.uiterlinden.android.hunted.task.BerichtTask;
import nl.uiterlinden.android.hunted.task.ControleTask;
import nl.uiterlinden.android.hunted.task.EindpuntTask;
import nl.uiterlinden.android.hunted.task.GaWegTask;
import nl.uiterlinden.android.hunted.task.GevondenTask;
import nl.uiterlinden.android.hunted.task.VertrekNaarTask;
import nl.uiterlinden.android.hunted.task.WisselTask;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SMS extends Activity {

	private HuntedService mBoundService;
	
	private Button btnAanmelden;
	private Button btnVertrek;
	private Button btnGevonden;
	private Button btnControle;
	private Button btnWissel;
	private Button btnGaWeg;
	private Button btnEindpunt;
	private Button btnBericht;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        setContentView(R.layout.sms);
        
        btnAanmelden = (Button) findViewById(R.id.btnAanmelden);
        btnAanmelden.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleTask(AanmeldTask.class);
            }
        });
        btnVertrek = (Button) findViewById(R.id.btnVertrek);
        btnVertrek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	handleTask(VertrekNaarTask.class);
            }
        });
        btnGevonden = (Button) findViewById(R.id.btnGevonden);
        btnGevonden.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	handleTask(GevondenTask.class);
            }
        });
        btnControle = (Button) findViewById(R.id.btnControle);
        btnControle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	handleTask(ControleTask.class);
            }
        });
        btnWissel = (Button) findViewById(R.id.btnWissel);
        btnWissel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	handleTask(WisselTask.class);
            }
        });
        btnGaWeg = (Button) findViewById(R.id.btnGaWeg);
        btnGaWeg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	handleTask(GaWegTask.class);
            }
        });
        btnEindpunt = (Button) findViewById(R.id.btnEindpunt);
        btnEindpunt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleTask(EindpuntTask.class);
            }
        });
        btnBericht = (Button) findViewById(R.id.btnBericht);
        btnBericht.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleTask(BerichtTask.class);
            }
        });
    }
    
    private void handleTask(Class taskClass) {
		try {
			Constructor c = taskClass.getConstructor(new Class[]{Context.class, HuntedService.class});
			AbstractTask task = (AbstractTask)c.newInstance(new Object[]{SMS.this, mBoundService});
			if (mBoundService == null) {
				Toast.makeText(this, "Toepassingsfout!.", Toast.LENGTH_SHORT).show();
				return;
			} 
			if (mBoundService.validatePreferences()) {
				task.handleTaskDialog(SMS.this);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    }
    
	void doBindService() {
		System.out.println("Bind hunted service...");
		getApplicationContext().bindService(new Intent(SMS.this, HuntedService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}
	
	void doUnbindService() {
		getApplicationContext().unbindService(mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			System.out.println("Hunted service connected....");
			mBoundService = ((HuntedService.LocalBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};
    
}
