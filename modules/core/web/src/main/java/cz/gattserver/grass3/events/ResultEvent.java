package cz.gattserver.grass3.events;

public interface ResultEvent extends Event {

	public boolean isSuccess();
	
	public String getResultDetails();
}
