package cs158project;

public final class Debug {
	public static void println(String title, String message) {
		System.out.println(
			String.format("[DEBUG %s] %s", title, message));
	}
}
