package nl.uiterlinden.android.hunted.domain;

public class LogItem {

	// 1 = IN, 2 = OUT
	public enum Direction { IN, OUT; 
		
		public int toInt() {
			if (IN.equals(this)) {
				return 1;
			} else if (OUT.equals(this)) {
				return 2;
			}
			return 0;
		}
	};
	
	public int id;
	public int direction;
	public String message;
	public long moment;
	
	public LogItem(Direction direction, String message) {
		this.direction = direction.toInt();
		this.message = message;
		this.moment = System.currentTimeMillis();
	}
	
	public LogItem(int id, int direction, String message, long moment) {
		this.id = id;
		this.direction = direction;
		this.message = message;
		this.moment = moment;
	}

	public int getId() {
		return id;
	}

	public int getDirection() {
		return direction;
	}

	public String getMessage() {
		return message;
	}

	public long getMoment() {
		return moment;
	}
	
	
}
