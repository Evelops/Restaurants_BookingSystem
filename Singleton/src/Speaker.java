
public class Speaker {
	private static Speaker speaker;
	private int volume;
	private Speaker() {
		volume = 5;
	}
	public static Speaker getInstance()
	{
		if(speaker == null)
		{
			speaker = new Speaker();
		}
		return speaker;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
}
