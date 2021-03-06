package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.models.reflections.Reflection;
import fr.fitoussoft.wapisdk.tasks.RequestNextReflectionsAsyncTask;
import fr.fitoussoft.wapitry.Application;
import fr.fitoussoft.wapitry.R;

public class ReflectionsActivity extends Activity {

    private List<Reflection> reflections;
    private String wac;
    private ArrayAdapter<Reflection> reflectionsAdapter;
    private ProgressBar progressBar;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Reflections onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflections);

        wac = getIntent().getExtras().getString("wac");

        if (progressBar == null) {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
        }

        progressBar.setVisibility(View.INVISIBLE);

        if (reflections == null) {
            reflections = new ArrayList<Reflection>();
        }

        if (reflectionsAdapter == null) {
            reflectionsAdapter = new ArrayAdapter<Reflection>(ReflectionsActivity.this, R.layout.reflection_item, reflections) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Get the data item for this position
                    Reflection reflection = getItem(position);

                    // Check if an existing view is being reused, otherwise inflate the view
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.reflection_item, parent, false);
                    }

                    // Lookup view for data population
                    TextView tvName = (TextView) convertView.findViewById(R.id.name);
                    TextView tvClassName = (TextView) convertView.findViewById(R.id.className);

                    // Populate the data into the template view using the data object
                    tvName.setText(reflection.getName());
                    tvClassName.setText(reflection.getClassName());

                    // Return the completed view to render on screen
                    return convertView;
                }
            };
        }

        ListView listView = (ListView) findViewById(R.id.reflections);
        listView.setOnScrollListener(new EndlessScrollListener());
        listView.setAdapter(reflectionsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reflections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.option_disconnect) {
            ((Application) getApplication()).getWapiClient().disconnect(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        reflections.clear();
        RequestNextReflectionsAsyncTask.nextSkipReflectionRequest = 0;
        executeRequestNextReflectionsAsyncTask();
    }

    private RequestNextReflectionsAsyncTask executeRequestNextReflectionsAsyncTask() {
        RequestNextReflectionsAsyncTask task = new RequestNextReflectionsAsyncTask(this) {
            @Override
            protected void onPostExecute(List<Reflection> reflections) {
                if (reflections != null) {
                    reflectionsAdapter.addAll(reflections);
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        task.getParams().put(RequestNextReflectionsAsyncTask.PARAM_WAC, wac);
        task.execute();
        return task;
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 2;
        private int previousTotal = 0;

        public EndlessScrollListener() {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                progressBar.setVisibility(View.VISIBLE);
                Log.d("[TRY]", "execute from scroll");
                executeRequestNextReflectionsAsyncTask();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
}

