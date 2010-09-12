package se.wendt.util;

public class ThreadUtils {

	private static final int MINIMUM_DELAY = 50;

	/**
	 * This method returns after no less than delay milliseconds.
	 * 
	 * @param delay milliseconds to sleep
	 */
	public static void sleep(final long delay) {
		long start = System.currentTimeMillis();
		long timeToReturn = start + delay;
		sleepUntil(timeToReturn);
	}
	
	/**
	 * This method returns no sooner than timeToReturns milliseconds have passed since the epoch.
	 * 
	 * @param timeToReturn when this method should return
	 */
	public static void sleepUntil(final long timeToReturn) {
		while (System.currentTimeMillis() < timeToReturn) {
			long millisUntilTimeIsUp = timeToReturn - System.currentTimeMillis();
			long delay = Math.max(MINIMUM_DELAY, millisUntilTimeIsUp);
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// this is ok
			}
		}
	}
}
