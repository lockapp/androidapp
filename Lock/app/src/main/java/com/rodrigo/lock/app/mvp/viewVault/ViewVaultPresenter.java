package com.rodrigo.lock.app.mvp.viewVault;

import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.ImageView;

import com.rodrigo.lock.app.bus.Event;
import com.rodrigo.lock.app.bus.EventBus;
import com.rodrigo.lock.app.bus.EventType;
import com.rodrigo.lock.app.data.Clases.VaultContent;
import com.rodrigo.lock.app.data.converters.Converter;
import com.rodrigo.lock.app.data.source.ContentVaultRepository;
import com.rodrigo.lock.app.util.schedulers.BaseSchedulerProvider;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 11/12/2016.
 */

public class ViewVaultPresenter implements ViewVaultContract.Presenter {

    // @NonNull
    // private final TasksRepository vaultRespository;

    @NonNull
    private final ViewVaultContract.View view;

    private ContentVaultRepository repository;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @Nullable
    private String vaultPath;
    @Nullable
    private String vaultPassword;
    private ArrayList<String> filesToAdd;

    @NonNull
    private CompositeSubscription mSubscriptions;
    private Subscription loadContentSubscription = null;


    public ViewVaultPresenter(String vaultPath,
                              String vaultPassword,
                              ArrayList<String> filesToAdd,
                              @NonNull ViewVaultContract.View view,
                              @NonNull BaseSchedulerProvider mSchedulerProvider) {
        this.view = view;
        this.mSchedulerProvider = mSchedulerProvider;
        this.vaultPath = vaultPath;
        this.vaultPassword = vaultPassword;
        this.filesToAdd=filesToAdd;

        mSubscriptions = new CompositeSubscription();
        this.view.setPresenter(this);

        loadContentRepository();
    }

    private void setTitle(){
        try{
            this.view.setTitle(Converter.convertVault(new File(vaultPath)).getName());
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    public void loadContentRepository() {
        ContentVaultRepository.openEncryptedFileSystem(vaultPath, vaultPassword)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<ContentVaultRepository>() {
                    @Override
                    public void onCompleted() {
                        if (filesToAdd!= null && !filesToAdd.isEmpty()){
                            view.addFilesToVault(filesToAdd);
                            view.finishActivity();
                        }else{
                            loadContentRepository(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorAlAbrirBobeda();
                    }

                    @Override
                    public void onNext(ContentVaultRepository repo) {
                        repository = repo;
                    }
                });
    }


    public void manejarEvento(Event event) {
        if (view.isActive() && vaultPath.equals(event.getVaultPath())) {
            if (event.getEventType() == EventType.TERMINO_ANIADIR_ARCHICOS) {
                loadContentRepository(false);
            }
        }
    }


    @Override
    public void subscribe() {
        setTitle();
        Subscription s = EventBus.getInstance().getEvents().subscribe(new Action1<Event>() {
            @Override
            public void call(Event event) {
                manejarEvento(event);
            }
        });
        mSubscriptions.add(s);
        loadContentRepository(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }


    public void loadContentRepository(final boolean showLoadingUI) {
        if (repository != null) {
            if (showLoadingUI) {
                view.setLoadingView();
            }
            //mSubscriptions.clear();
            if (loadContentSubscription != null) {
                loadContentSubscription.unsubscribe();
            }

            loadContentSubscription = repository.getFiles()
                    .subscribeOn(mSchedulerProvider.computation())
                    .observeOn(mSchedulerProvider.ui())
                    .subscribe(new Observer<List<VaultContent>>() {
                        @Override
                        public void onCompleted() {
                            //view.setContentView();
                        }

                        @Override
                        public void onError(Throwable e) {
                            /*mVaultsView.showLoadingTasksError();*/
                        }

                        @Override
                        public void onNext(List<VaultContent> archivos) {
                            setResultArchivos(archivos);
                        }
                    });
            mSubscriptions.add(loadContentSubscription);
        }
    }

    public void errorAlAbrirBobeda() {
        view.noSePudoAbrirBobeda();
    }


    private void setResultArchivos(List<VaultContent> archivos) {
        if (view.isActive()) {
            // Show the list of tasks
            view.setContenToShow(archivos);
            view.setContentView();

        }
    }


    public void loadPreview(final WeakReference<ImageView> imageViewWeakReference, VaultContent vaultContent) {
        Subscription subscription = repository.getPreview(vaultContent.getId())
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                            /*mVaultsView.showLoadingTasksError();*/
                    }

                    @Override
                    public void onNext(Bitmap preview) {
                        setResultImage(imageViewWeakReference, preview);

                    }
                });
        mSubscriptions.add(subscription);
    }


    public void openContent(final VaultContent vaultContent) {
        vaultContent.setExtrayendo(true);
        view.cambioExtrayendoEnItem(vaultContent);

        Subscription subscription = repository.getFile(vaultContent.getId())
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<Uri>() {
                    @Override
                    public void onCompleted() {
                        vaultContent.setExtrayendo(false);
                        view.cambioExtrayendoEnItem(vaultContent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("VerVault", "error al abrir contenido", e);
                            /*mVaultsView.showLoadingTasksError();*/
                    }

                    @Override
                    public void onNext(Uri uri) {
                        String extendion = FileUtils.getExtensionFile(vaultContent.getFullPath());
                        view.openFile(extendion, uri);

                    }
                });
        mSubscriptions.add(subscription);
    }


    private void setResultImage(WeakReference<ImageView> imageViewWeakReference, Bitmap myBitmap) {
        ImageView iv = imageViewWeakReference.get();
        if (iv != null) {
            if (myBitmap != null) {
                iv.setImageBitmap(myBitmap);
            }
        }
    }



/*
    public void extractContent (final VaultContent vaultContent){
        vaultContent.setExtrayendo(true);
        view.cambioExtrayendoEnItem(vaultContent);
        Subscription subscription = repository.extractFile(vaultContent.getId())
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onCompleted() {
                        vaultContent.setExtrayendo(false);
                        view.cambioExtrayendoEnItem(vaultContent);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(File file) {
                        String extendion = FileUtils.getExtensionFile(vaultContent.getFullPath());
                        view.openFile(extendion, Uri.fromFile(file));

                    }
                });
        mSubscriptions.add(subscription);
    }
*/

    public void delete(final VaultContent vaultContent) {
        Subscription subscription = repository.deleteFile(vaultContent.getId())
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                            /*mostrar error que no se pudo eliminar*/
                    }

                    @Override
                    public void onNext(File file) {
                    }
                });
        //mSubscriptions.add(subscription);
    }

    @Override
    public String getPassword() {
        return vaultPassword;
    }

    @Override
    public String getVaultPath() {
        return vaultPath;
    }

    @Override
    public void clrearCache (){
        if (this.repository!= null){
            try {
                repository.clearCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public void deleteVault() {
        try {
            repository.removeFromUso();
            FileUtils.delete(new File(vaultPath));
            view.finishActivity();
        }catch (Exception e){
            view.errorToDelete();
        }
        //mSubscriptions.add(subscription);
    }

}
