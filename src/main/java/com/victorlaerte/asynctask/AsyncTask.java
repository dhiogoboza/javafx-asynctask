package com.victorlaerte.asynctask;

import javafx.application.Platform;
import java.lang.Runnable;
import java.lang.Throwable;

/**
 *
 * @author Victor Oliveira
 */
public abstract class AsyncTask {

	private boolean daemon = true;

	public abstract void onPreExecute();

	public abstract void doInBackground() throws Throwable;

	public abstract void onPostExecute();

	public abstract void progressCallback(Object... params);
	
	public abstract void onFail(Throwable t);

	public void publishProgress(final Object... params) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				progressCallback(params);
			}
		});
	}

	private final Thread backGroundThread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				doInBackground();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						onPostExecute();
					}
				});
			} catch (final Throwable e) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						onFail(e);
					}
				});
				
			}
		}
	});

	public void execute() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				onPreExecute();

				backGroundThread.setDaemon(daemon);
				backGroundThread.start();
			}
		});
	}

	public void setDaemon(boolean daemon) {

		this.daemon = daemon;
	}

	public void interrupt() {

		this.backGroundThread.interrupt();
	}
}
