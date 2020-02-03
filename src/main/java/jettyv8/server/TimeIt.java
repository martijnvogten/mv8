package jettyv8.server;

public class TimeIt {

	public static void time(String description, Runnable task) {
		long before = System.nanoTime();
		task.run();
		System.out.println(description + " took " + ((System.nanoTime() - before) / 1_000) + " µs");
	}

}
