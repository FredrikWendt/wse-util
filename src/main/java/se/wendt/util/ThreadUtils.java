package se.wendt.util;

public class ThreadUtils {

	/**
	 * This method returns after no less than delay milliseconds.
	 * 
	 * @param delay milliseconds to sleep
	 */
	public static void sleep(long delay) {
		long start = System.currentTimeMillis();
		long timeToReturn = start + delay;
		while (System.currentTimeMillis() < timeToReturn) {
			delay = Math.max(50, timeToReturn - System.currentTimeMillis());
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// this is ok
			}
		}
	}
}
