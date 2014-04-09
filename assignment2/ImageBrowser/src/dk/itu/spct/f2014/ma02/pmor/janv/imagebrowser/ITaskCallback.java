package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.lang.reflect.Method;

public interface ITaskCallback<Result> {

	/**
	 * Invoked on the callback listener when a {@link SignalrTask} has completed.
	 * @param m The method that was invoked (i.e. the actual work of the task)
	 * @param result The return value of {@code m}.
	 */
	void onTaskCompleted(Method m, Result result);

}
