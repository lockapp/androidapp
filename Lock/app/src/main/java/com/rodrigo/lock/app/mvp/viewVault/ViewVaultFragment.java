package com.rodrigo.lock.app.mvp.viewVault;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.addFileToVault.AddFileToVaultService;
import com.rodrigo.lock.app.data.Clases.VaultContent;
import com.rodrigo.lock.app.data.source.Preferences;
import  com.rodrigo.lock.app.extract.ExtractService;
import com.rodrigo.lock.app.utils.ActivityUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 11/12/2016.
 */

public class ViewVaultFragment extends Fragment implements ViewVaultContract.View {
    private static final String ARGUMENT_PASSWORD = "PASSWORD";
    private static final String ARGUMENT_FULL_PATH = "FULL_PATH";

    private static final int REQUEST_CHOOSER = 1234;


    @BindView(R.id.cargando)
    View cargando;
    @BindView(R.id.contenidoPrincipal)
    View contenidoPrincipal;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_add_task)
    FloatingActionButton fab;

    private ViewVaultContract.Presenter mPresenter;
    Recycler_View_Adapter mAdapter;


    public static ViewVaultFragment newInstance(String fullPath, String password) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_FULL_PATH, fullPath);
        arguments.putString(ARGUMENT_PASSWORD, password);
        ViewVaultFragment fragment = new ViewVaultFragment();
        fragment.setArguments(arguments);
        return fragment;
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
    public void onDestroy() {
        super.onDestroy();
        mPresenter.clrearCache();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.view_vault_frag, container, false);
        ButterKnife.bind(this, root);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        //inicializa actionbar
        ViewVaultActivity activity = (ViewVaultActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //inicializa recycler veiw
        mAdapter = new Recycler_View_Adapter();
        mAdapter.setContext(this.getActivity().getApplicationContext());
        loadLayautManager();

        //inicialza boton
        fab.setImageResource(R.drawable.z_ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);
            }
        });

        return root;
    }

    StaggeredListDecoration lastDecorator = null;
    public void loadLayautManager(){
        boolean isGridView = Preferences.isGridViewInVault();
        mAdapter.setGridView(isGridView);
        if (lastDecorator!=null){
            recyclerView.removeItemDecoration(lastDecorator);
        }
        //recyclerView.addItemDecoration(new StaggeredListDecoration());
        if (isGridView){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            lastDecorator = new StaggeredListDecoration(4);
            recyclerView.addItemDecoration(lastDecorator);
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext()));
            lastDecorator = new StaggeredListDecoration(0);
            recyclerView.addItemDecoration(lastDecorator);
           // recyclerView.addItemDecoration(new StaggeredListDecoration(0));
        }
        recyclerView.setAdapter(mAdapter);


       //
    }

    MaterialTapTargetPrompt mFabPrompt;
    public void showEmptyListMessage(){
        if (mFabPrompt != null){
            return;
        }
        mFabPrompt =new MaterialTapTargetPrompt.Builder(this.getActivity(),  R.style.MaterialTapTargetPromptTheme_FabTarget)
                .setTarget(fab)
                .setAutoDismiss(false)
                //.setAutoFinish(false)
                //.setCaptureTouchEventOutsidePrompt(true)
                .setPrimaryText(getResources().getString(R.string.contenido_bobeda_vacio_titulo))
                .setSecondaryText(getResources().getString(R.string.contenido_bobeda_vacio_descripcion))
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener(){
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
                })
                .show();
    }
    public void hideEmptyListMessage(){
        if (mFabPrompt != null){
            mFabPrompt.finish();
            mFabPrompt = null;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri uri = data.getData();
                    // Get the File path from the Uri
                    String path = FileUtils.getPath(getContext(), uri);
                    // Alternatively, use FileUtils.getFile(Context, Uri)
                    if (path != null && FileUtils.isLocal(path)) {
                        addFilesToVault (new ArrayList<String>( Arrays.asList(path)));

                    }
                }
                break;
        }
    }

    @Override
    public void addFilesToVault(ArrayList<String> archivos){
        Intent i = new Intent(getContext(), AddFileToVaultService.class);
        i.putExtra(AddFileToVaultService.EXTRA_VAULT_PATH, mPresenter.getVaultPath());
        i.putExtra(AddFileToVaultService.EXTRA_VAULT_PASSWORD, mPresenter.getPassword());
        i.putStringArrayListExtra(AddFileToVaultService.EXTRA_ARCHIVOS, archivos);
        getContext().startService(i);
    }

    @Override
    public void errorToDelete() {
        Toast.makeText(getActivity(), getString(R.string.error_delete_file), Toast.LENGTH_SHORT).show();
    }

    public  void extraer (VaultContent conetent){
        Intent i = new Intent(getContext(), ExtractService.class);
        i.putExtra(ExtractService.EXTRA_VAULT_PATH, mPresenter.getVaultPath());
        i.putExtra(ExtractService.EXTRA_VAULT_PASSWORD, mPresenter.getPassword());
        i.putExtra(ExtractService.EXTRA_ID_ARCHIVO, conetent.getId());
        getContext().startService(i);
    }

    @Override
    public void setPresenter(@NonNull ViewVaultContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_lista:
                Preferences.saveGridViewInVault(false);
                loadLayautManager();
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.menu_grid:
                Preferences.saveGridViewInVault(true);
                loadLayautManager();
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.share:
                Intent sendIntent = ActivityUtils.shareExludingApp(getContext(), Uri.fromFile(new File(mPresenter.getVaultPath())));
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                return true;

            case R.id.delete:
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.delete))
                        .setMessage(getString(R.string.delete_message))
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                                mPresenter.deleteVault();
                                getActivity().finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
        }
        return false;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (Preferences.isGridViewInVault()){
            inflater.inflate(R.menu.z_view_content_vault_grid, menu);
        }else{
            inflater.inflate(R.menu.z_view_content_vault_list, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }
    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void noSePudoAbrirBobeda() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setLoadingView() {
        cargando.setVisibility(View.VISIBLE);
        contenidoPrincipal.setVisibility(View.GONE);

    }

    @Override
    public void setContentView() {
        contenidoPrincipal.setVisibility(View.VISIBLE);
        cargando.setVisibility(View.GONE);
    }

    @Override
    public void setContenToShow(List<VaultContent> contenToShow) {
        mAdapter.setList(contenToShow);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void openFile(String extension, Uri fileUri) {
        try {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(extension);
            newIntent.setDataAndType(fileUri, mimeType);
            newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(newIntent);
        } catch (Exception e) {
            Intent altIntent = new Intent(android.content.Intent.ACTION_VIEW);
            altIntent.setData(fileUri);
            startActivity(altIntent);
        }
    }


    @Override
    public void cambioExtrayendoEnItem (VaultContent content){
        this.mAdapter.notificarCambioEnItem(content);
    }


    public void removeItem(final int position, final VaultContent data) {
        mAdapter.remove(data);
        Snackbar.make(recyclerView, R.string.notice_removed, Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        //get position from view item of snackbar that you set it before.
                        //int position = (int) snackbar.getView().getTag();
                        switch (event) {
                            case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                mAdapter.insert(position, data);
                                break;
                            default:
                                mPresenter.delete(data);
                                break;
                        }
                    }
                })
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do nothing
                    }
                })
                .show();
    }


    public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {
        boolean isGridView;

        View.OnClickListener clickRow = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                VaultContent item = list.get(itemPosition);
                ViewVaultFragment.this.mPresenter.openContent(item);
            }
        };


        List<VaultContent> list = Collections.emptyList();
        Context context;


        public void setList(List<VaultContent> list) {
            this.list = list;
            if (list.isEmpty()){
                showEmptyListMessage();
            }else{
                hideEmptyListMessage();
            }
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setGridView(boolean gridView) {
            isGridView = gridView;
        }

        @Override
        public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate the layout, initialize the View Holder
            View v = LayoutInflater.from(parent.getContext()).inflate(isGridView?R.layout.view_vault_row_grid : R.layout.view_vault_row_list, parent, false);
            View_Holder holder = new View_Holder(v, isGridView);
            v.setOnClickListener(clickRow);
            return holder;

        }

        @Override
        public void onBindViewHolder(View_Holder holder, int position) {
            //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
            VaultContent elemento = list.get(position);
            holder.title.setText(elemento.getFullPath());
            holder.description.setText(elemento.getSize());
            holder.masOpciones.setOnClickListener(new MenuCallback(position, elemento));
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.z_circle_img_bg, null);
            holder.imageView.setImageDrawable(drawable);
            if (elemento.isExtrayendo()){
                holder.hideWhenLoad.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
            }else{
                holder.progressBar.setVisibility(View.GONE);
                holder.hideWhenLoad.setVisibility(View.VISIBLE);
            }
            if (elemento.isEsVideo() ){
                holder.playIcon.setVisibility(View.VISIBLE);
            }else{
                holder.playIcon.setVisibility(View.GONE);
            }
            //se carga la imagen
            ViewVaultFragment.this.mPresenter.loadPreview(new WeakReference<ImageView>(holder.imageView), elemento);

        }

        @Override
        public int getItemCount() {
            //returns the number of elements the RecyclerView will display
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        // Insert a new item to the RecyclerView on a predefined position
        public void insert(int position, VaultContent data) {
            list.add(position, data);
            notifyItemInserted(position);
            hideEmptyListMessage();
        }

        // Remove a RecyclerView item containing a specified Data object
        public void remove(VaultContent data) {
            int position = list.indexOf(data);
            list.remove(position);
            notifyItemRemoved(position);
            if (list.isEmpty()){
                showEmptyListMessage();
            }
        }

        public void notificarCambioEnItem(VaultContent data){
            int position = list.indexOf(data);
            notifyItemChanged(position);
        }


    }


    public class View_Holder extends RecyclerView.ViewHolder {
        //CardView cv;
        public TextView title;
        public TextView description;
        public ImageView imageView;
        public ImageView masOpciones;
        public View progressBar;
        public View playIcon;
        public View hideWhenLoad;

        View_Holder(View itemView, boolean isGridView) {
            super(itemView);
            //cv = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            masOpciones = (ImageView) itemView.findViewById(R.id.mas_opciones);
            progressBar = (View) itemView.findViewById(R.id.progressBar);
            playIcon = (View) itemView.findViewById(R.id.playIcon);
            if (isGridView){
                hideWhenLoad = (View) itemView.findViewById(R.id.maincontent);
            }else{
                hideWhenLoad= description;
            }
        }
    }


    public class MenuCallback implements View.OnClickListener {
        VaultContent content;
        int position;

        public MenuCallback(int position, VaultContent item) {
            this.content = item;
            this.position = position;
        }

        private MenuBuilder.Callback clickItemsInPopUp = new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.extraer:
                        ViewVaultFragment.this.extraer(content);
                        break;
                    case R.id.eliminar:
                        ViewVaultFragment.this.removeItem(position, content);
                        break;
                }
                return true;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        };


        @Override
        public void onClick(View v) {
            Context context = ViewVaultFragment.this.getActivity();
            MenuBuilder menuBuilder = new MenuBuilder(context);
            menuBuilder.setCallback(clickItemsInPopUp);
            MenuInflater inflater = new MenuInflater(context);
            inflater.inflate(R.menu.z_popup_menu_item, menuBuilder);
            MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, v);
            optionsMenu.setForceShowIcon(true);
            optionsMenu.show();

        }
    }


}
