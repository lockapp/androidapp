package com.rodrigo.lock.app.mvp.listVaults;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.mvp.createVault.CreateEditVaultActivity;
import com.rodrigo.lock.app.mvp.openVault.OpenVaultActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public class VaultsFragment extends Fragment implements VaultsContract.View  {

    @BindView(R.id.tasks_list) ListView listView;


    private VaultsContract.Presenter mPresenter;

    private TasksAdapter mListAdapter;

    //@InjectView(R.id.noTasks) View mNoTasksView;

    //@InjectView(R.id.noTasksMain)  TextView mNoTaskMainView;

    //@InjectView(R.id.noTasksAdd)  TextView mNoTaskAddView;

    @BindView(R.id.tasksLL) LinearLayout mTasksView;
    FloatingActionButton fab;


    public VaultsFragment() {
        // Requires empty public constructor
    }

    public static VaultsFragment newInstance() {
        return new VaultsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TasksAdapter(new ArrayList<Vault>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull VaultsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.vaults_frag, container, false);
        ButterKnife.bind(this, root);

        // Set up tasks view
        listView.setAdapter(mListAdapter);
        //mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        //mTasksView = (LinearLayout) root.findViewById(R.id.tasksLL);

        // Set up  no tasks view
        //mNoTasksView = root.findViewById(R.id.noTasks);
        //mNoTaskIcon = (ImageView) root.findViewById(R.id.noTasksIcon);
        //mNoTaskMainView = (TextView) root.findViewById(R.id.noTasksMain);
        //mNoTaskAddView = (TextView) root.findViewById(R.id.noTasksAdd);
        /*mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showAddTask();
            }
        });*/

        // Set up floating action button
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);

        fab.setImageResource(R.drawable.z_ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateEditVaultActivity.class);
                startActivity(intent);
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadVaults();
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_clear:
//                mPresenter.clearCompletedTasks();
//                break;
//            case R.id.menu_filter:
//                showFilteringPopUpMenu();
//                break;
//            case R.id.menu_refresh:
//                mPresenter.loadTasks(true);
//                break;
//        }
        return true;
    }





    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTasks(List<Vault> tasks) {
        mListAdapter.replaceData(tasks);
        if (mFabPrompt != null){
            mFabPrompt.finish();
            mFabPrompt = null;
        }
        //mTasksView.setVisibility(View.VISIBLE);
        //mNoTasksView.setVisibility(View.GONE);
    }


    MaterialTapTargetPrompt mFabPrompt;
    @Override
    public void showNoVaults() {
        if (mFabPrompt != null){
            return;
        }
        mFabPrompt =new MaterialTapTargetPrompt.Builder(this.getActivity(),  R.style.MaterialTapTargetPromptTheme_FabTarget)
                .setTarget(fab)
                .setAutoDismiss(false)
                //.setAutoFinish(false)
                //.setCaptureTouchEventOutsidePrompt(true)
                .setPrimaryText(getResources().getString(R.string.lista_bobeda_vacio_titulo))
                .setSecondaryText(getResources().getString(R.string.lista_bobeda_vacio_descripcion))
                /*.setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener(){
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget){
                        //Do something such as storing a value so that this prompt is never shown again
                        if (tappedTarget){
                            mFabPrompt.finish();
                            mFabPrompt = null;
                        }
                    }
                    @Override
                    public void onHidePromptComplete(){

                    }
                })*/
                .show();
    }


    @Override
    public void showSuccessfullySavedMessage() {
      //  showMessage(getString(R.string.successfully_saved_task_message));
    }
/*
    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        //mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }*/

    @Override
    public void showLoadingTasksError() {
        showMessage(getString(R.string.z_loading_tasks_error));
    }


    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private static class TasksAdapter extends BaseAdapter {

        private List<Vault> mTasks;
        private TaskItemListener mItemListener;

        public TasksAdapter(List<Vault> tasks, TaskItemListener itemListener) {
            setList(tasks);
            mItemListener = itemListener;
        }

        public void replaceData(List<Vault> tasks) {
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Vault> tasks) {
            mTasks = checkNotNull(tasks);
        }

        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Vault getItem(int i) {
            return mTasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.vault_item, viewGroup, false);
            }

            final Vault task = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(task.getName());


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onTaskClick(task);
                }
            });

            return rowView;
        }
    }

    /**
     * Listener for clicks on tasks in the ListView.
     */
    protected TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Vault clickedVault) {
            Intent intent = new Intent(getContext(), OpenVaultActivity.class);
            intent.putExtra(OpenVaultActivity.EXTRA_VAULT_PATH, clickedVault.getFullPath());
            startActivity(intent);
        }
    };

    public void setMItemListener(TaskItemListener mItemListener){
        this.mItemListener = mItemListener;
    }



    public interface TaskItemListener {

        void onTaskClick(Vault clickedTask);

    }

}