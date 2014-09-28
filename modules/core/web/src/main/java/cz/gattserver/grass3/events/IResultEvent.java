package cz.gattserver.grass3.events;

public interface IResultEvent extends IEvent {

	public boolean isSuccess();
	
	public String getResultDetails();
}
