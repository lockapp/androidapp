package com.rodrigo.lock.app.mvp.listVaults;

import android.content.Intent;
import android.net.Uri;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rodrigo.lock.app.mvp.backup.BackupActivity;
import com.rodrigo.lock.app.mvp.preguntasFrecuentes.PreguntasFrecuentes;
import com.rodrigo.lock.app.utils.Injection;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.utils.ActivityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


import butterknife.BindView;
import butterknife.ButterKnife;

public class VaultsActivity extends AppCompatActivity {


    protected VaultPresenetr mTasksPresenter;

    @BindView(R.id.toolbar)   Toolbar toolbar;
    @BindView(R.id.drawer_layout)   DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)  NavigationView navigationView;

    //Wed May 16 2018 17:15:20 GMT-0300 (Local Standard Time)
    private Date fechaHabilitaPagos = new Date(1526501720653L);
    //private Date fechaHabilitaPagos = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaults_activity);
        ButterKnife.bind(this);
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

/*
        Date actual = new Date();
        if (actual.compareTo(fechaHabilitaPagos) < 0){
            hideDonar();
        }*/
        hideDonar();
    }


    private void hideDonar() {
        try{
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.donar).setVisible(false);
        }catch (Throwable t){

        }
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
                            case R.id.donar:
                                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7R9PXAXWHZ8HU")));
                                break;
                            case R.id.backup:
                                startActivity(new Intent(getApplicationContext(), BackupActivity.class));
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



    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }
    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject(){{      put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject(){{        put("gateway", "example");
                put("gatewayMerchantId", "exampleGatewayMerchantId");
            }
            });
        }};
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }


    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
      //  parameters.put("billingAddressRequired", true);

        JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);

        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }


/*
    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(Constants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }


    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }
*/


}
