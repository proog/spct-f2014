package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.AsyncTask;

public class SignalrTask<Result> extends AsyncTask<Object, Void, Result> {

	private final NetworkClient client;
	private final Method m;
	private final ITaskCallback<Result> callback;
	
	public SignalrTask(NetworkClient client, Method m, ITaskCallback<Result> callback) {
		if(client == null)
			throw new NullPointerException("client cannot be null");
		if(m == null)
			throw new NullPointerException("method cannot be null");
		if(callback == null)
			throw new NullPointerException("callback cannot be null");
		this.client = client;
		this.m = m;
		this.callback = callback;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Result doInBackground(Object...  args) {
		try {
			return (Result) m.invoke(client, args);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		callback.onTaskCompleted(m, result);
	}
}
