package nl.uiterlinden.android.hunted.data;

public interface ProgressMonitor {

	public void update(int progress);

	public void setMaxProgress(int size);
	
}
