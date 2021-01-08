public class Main {
	public static void main(String[] args) {
		Speaker speaker1 = Speaker.getInstance();
		Speaker speaker2 = Speaker.getInstance();
		Speaker speaker3 = Speaker.getInstance();
		System.out.println(speaker1);
		System.out.println(speaker2);
		System.out.println(speaker3);
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
		speaker1.setVolume(10);
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
		System.out.println(speaker3.getVolume());
		speaker2.setVolume(20);
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
		System.out.println(speaker3.getVolume());
	}

}
