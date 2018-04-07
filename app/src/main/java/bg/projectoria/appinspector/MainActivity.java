package bg.projectoria.appinspector;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * An activity representing a list of apps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DetailsActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;
    private Selection selection;
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // FIXME
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        if (findViewById(R.id.app_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }

        selection = new Selection(savedInstanceState);

        RecyclerView recyclerView = findViewById(R.id.app_list);
        View progress = findViewById(R.id.progress);
        setup(recyclerView, progress);
    }

    private void setup(@NonNull RecyclerView recyclerView, @NonNull View progress) {
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL));
        adapter = new AppAdapter(selection, this, twoPane);
        recyclerView.setAdapter(adapter);

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getApps().observe(this, apps -> {
            if (apps != null) {
                progress.setVisibility(View.GONE);
            }
            adapter.setApps(apps);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        selection.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

        private final Selection selection;
        private final MainActivity parent;
        private final boolean twoPane;
        private List<AppStub> apps;

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) view.getTag();
                int position = holder.getAdapterPosition();
                selection.set(position);
                AppStub stub = apps.get(position);
                if (twoPane) {
                    parent.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.app_detail_container,
                                    DetailsFragment.make(stub.packageName))
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra(DetailsFragment.PACKAGE_NAME, stub.packageName);

                    context.startActivity(intent);
                }
            }
        };

        AppAdapter(Selection selection, MainActivity parent, boolean twoPane) {
            this.selection = selection;
            this.parent = parent;
            this.twoPane = twoPane;
            this.apps = Collections.emptyList();
        }

        void setApps(List<AppStub> apps) {
            this.apps = apps;
            notifyDataSetChanged();
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.app_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AppStub stub = apps.get(position);
            holder.icon.setImageDrawable(stub.icon);
            holder.label.setText(stub.label);

            holder.itemView.setTag(holder);
            holder.itemView.setOnClickListener(onClickListener);

            if (twoPane) {
                holder.itemView.setActivated(selection.isSelected(position));
            }
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView icon;
            final TextView label;

            ViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.icon);
                label = view.findViewById(R.id.label);
            }
        }
    }

    private class Selection {
        private int current = -1;

        Selection(Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                this.current = savedInstanceState.getInt("selection_pos", this.current);
            }
        }

        void onSaveInstanceState(Bundle outState) {
            outState.putInt("selection_pos", current);
        }

        boolean isSelected(int position) {
            return this.current == position;
        }

        void set(int position) {
            if (this.current == position) {
                return;
            }

            int previous = current;
            current = position;

            if (twoPane && (position >= 0)) {
                adapter.notifyItemChanged(previous);
                adapter.notifyItemChanged(position);
            }
        }
    }
}
