package bg.projectoria.appinspector;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single App detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link DetailsActivity}
 * on handsets.
 */
public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    public static final String PACKAGE_NAME = "package_name";

    private static class Model {
        ApplicationInfo app;
        PackageInfo pkg;
        String appLabel;
    }

    private Model model;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailsFragment() {
    }

    @SuppressLint("ValidFragment")
    public DetailsFragment(String packageName) {
        Bundle args = new Bundle();
        args.putString(
                DetailsFragment.PACKAGE_NAME,
                packageName);
        setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(PACKAGE_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String packageName = getArguments().getString(PACKAGE_NAME);
            PackageManager pman = getActivity().getPackageManager();

            model = new Model();

            try {
                model.app = pman.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                model.pkg = pman.getPackageInfo(packageName, PackageManager.GET_META_DATA);
                model.appLabel = String.valueOf(pman.getApplicationLabel(model.app));
            }
            catch (PackageManager.NameNotFoundException e) {
                // FIXME
                Log.e(TAG, "error", e);
                return;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(model.appLabel);
        }

        View rootView = inflater.inflate(R.layout.details_fragment, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.app_detail_list);
        setupRecyclerView(recyclerView);

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new DetailAdapter(model));
    }

    private static class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

        private final List<Pair<String, String>> entries = new ArrayList<>();

        DetailAdapter(Model model) {
            entry("Label", model.appLabel);
            entry("Package name", model.app.packageName);
            entry("Version name", model.pkg.versionName);
            entry("Version code", model.pkg.versionCode);
            entry("Target SDK version", model.app.targetSdkVersion);
            entry("Class name", model.app.className);
            entry("Source dir", model.app.sourceDir);
            entry("Data dir", model.app.dataDir);
        }

        private void entry(String label, Object value) {
            entries.add(Pair.create(label, String.valueOf(value)));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Pair<String,String> entry = entries.get(position);
            holder.label.setText(entry.first);
            holder.value.setText(entry.second);
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView label;
            final TextView value;

            ViewHolder(View view) {
                super(view);
                label = view.findViewById(android.R.id.text1);
                value = view.findViewById(android.R.id.text2);
            }
        }
    }
}
