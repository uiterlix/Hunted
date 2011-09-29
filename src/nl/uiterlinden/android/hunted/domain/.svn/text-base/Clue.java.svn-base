package nl.uiterlinden.android.hunted.domain;

import java.util.Date;

public class Clue {

	String name;
	String coordinate;
	String word;
	Date found;
	
	public Clue(String name, String coordinate) {
		super();
		this.name = name;
		this.coordinate = coordinate;
	}
	
	public Clue(String name, String coordinate, String word, long timeFound) {
		this.name = name;
		this.coordinate = coordinate;
		this.word = word;
		if (timeFound > 0) { 
			this.found = new Date(timeFound);
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Date getFound() {
		return found;
	}
	public void setFound(Date found) {
		this.found = found;
	}

	@Override
	public String toString() {
		return "Clue [name=" + name + ", coordinate=" + coordinate + ", word="
				+ word + ", found=" + found + "]";
	}
	
	
}
