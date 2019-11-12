package course.examples.Threading.ThreadingAsyncTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class AsyncTaskActivity extends Activity {
	
	private final static String TAG = "ThreadingAsyncTask";
	
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mImageView = findViewById(R.id.imageView);
		mProgressBar = findViewById(R.id.progressBar);
		
		final Button button = findViewById(R.id.loadButton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new LoadIconTask(AsyncTaskActivity.this).execute(R.drawable.painter);
			}
		});
		
		final Button otherButton = findViewById(R.id.otherButton);
		otherButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(AsyncTaskActivity.this, "I'm Working",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	private static class LoadIconTask extends AsyncTask<Integer, Integer, Bitmap> {

		private final int mDelay = 500;
		private final WeakReference<AsyncTaskActivity> activityReference;

		// only retain a weak reference to the activity
		LoadIconTask(AsyncTaskActivity context) {
			activityReference = new WeakReference<>(context);
		}

		@Override
		protected void onPreExecute() {
			// get a reference to the activity if it is still there
			AsyncTaskActivity activity = activityReference.get();
			if (activity == null || activity.isFinishing()) return;

			activity.mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(Integer... resId) {
			// get a reference to the activity if it is still there
			AsyncTaskActivity activity = activityReference.get();
			if (activity == null || activity.isFinishing()) return null;

			Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), resId[0]);
			// simulating long-running operation 
			for (int i = 1; i < 11; i++) {
				sleep();
				publishProgress(i * 10);
			}
			return tmp;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// get a reference to the activity if it is still there
			AsyncTaskActivity activity = activityReference.get();
			if (activity == null || activity.isFinishing()) return;

			activity.mProgressBar.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// get a reference to the activity if it is still there
			AsyncTaskActivity activity = activityReference.get();
			if (activity == null || activity.isFinishing()) return;

			activity.mProgressBar.setVisibility(ProgressBar.INVISIBLE);
			activity.mImageView.setImageBitmap(result);
		}

		private void sleep() {
			try {
				Thread.sleep(mDelay);
			} catch (InterruptedException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
}