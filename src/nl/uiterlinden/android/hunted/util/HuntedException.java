package nl.uiterlinden.android.hunted.util;

public class HuntedException extends RuntimeException {
	private static final long serialVersionUID = 419801788656367083L;

	public HuntedException() {

	}

	public HuntedException(String arg0) {
		super(arg0);
	}

	public HuntedException(Throwable arg0) {
		super(arg0);
	}

	public HuntedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
