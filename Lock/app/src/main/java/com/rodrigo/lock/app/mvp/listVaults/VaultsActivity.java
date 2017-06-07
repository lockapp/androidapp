package com.rodrigo.lock.app.mvp.listVaults;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.rodrigo.lock.app.mvp.preguntasFrecuentes.PreguntasFrecuentes;
import com.rodrigo.lock.app.utils.Injection;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.utils.ActivityUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VaultsActivity extends AppCompatActivity {


    protected VaultPresenetr mTasksPresenter;

    @InjectView(R.id.toolbar)   Toolbar toolbar;
    @InjectView(R.id.drawer_layout)   DrawerLayout mDrawerLayout;
    @InjectView(R.id.nav_view)  NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaults_activity);
        ButterKnife.inject(this);
        // Set up the toolbar.
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.z_ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the navigation drawer.
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        VaultsFragment tasksFragment =    (VaultsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = VaultsFragment.newInstance();
            ActivityUtils.addFragmentToActivity( getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        // Create the presenter
        mTasksPresenter = new VaultPresenetr(
                tasksFragment,
                Injection.provideSchedulerProvider());


    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putSerializable(CURRENT_FILTERING_KEY, mTasksPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.preguntasFrecuentes:
                                Intent intentPF =new Intent(VaultsActivity.this, PreguntasFrecuentes.class);
                                startActivity(intentPF);
                                break;
                            case R.id.compartir:
                                Intent intentShare = new Intent(Intent.ACTION_SEND);
                                intentShare.setType("text/plain");
                                intentShare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.rodrigo.lock.app");
                                intentShare.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
                                startActivity(Intent.createChooser(intentShare, "Share"));
                                break;
                            case R.id.rate:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.rodrigo.lock.app")));
                                break;
                            case R.id.open_web_site:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://lockapp.github.io")));
                                break;
                            case R.id.contacto:
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"lock.app.android@gmail.com"});
                                // i.putExtra(Intent.EXTRA_SUBJECT, "from android");
                                //i.putExtra(Intent.EXTRA_TEXT   , getResources().getString(R.string.enterhere));
                                try {
                                    startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(VaultsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

}
