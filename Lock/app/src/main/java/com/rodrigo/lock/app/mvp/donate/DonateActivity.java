package com.rodrigo.lock.app.mvp.donate;

import android.os.Bundle;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rodrigo.lock.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DonateActivity extends AppCompatActivity {

    private BillingClient billingClient;
    private View loadingView;
    private View errorview;
    private View mianview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        loadingView = findViewById(R.id.loadingview);
        errorview = findViewById(R.id.errorview);
        mianview = findViewById(R.id.mianview);
        Button reintentar = findViewById(R.id.reintentar);
        reintentar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                inicializarDonar();
            }
        });

        showLoadingView();
        inicializarDonar();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showMainView(){
        mianview.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        errorview.setVisibility(View.GONE);
    }


    private void showErrorView(){
        mianview.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorview.setVisibility(View.VISIBLE);
    }

    private void showLoadingView(){
        mianview.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorview.setVisibility(View.GONE);
    }


    private  void inicializarDonar(){
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    mostrarListaDeProductos();
                }else{
                    showErrorView();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                showErrorView();
            }
        });
    }





    private void mostrarListaDeProductos(){
        //mostrar productos
        List<String> skuList = new ArrayList<>();
        skuList.add("donate5");
        skuList.add("donate25");
        skuList.add("donate50");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        //skuDetailsList.get(0).get
                        LinearLayout donateList = (LinearLayout) findViewById(R.id.donateList);
                        donateList.removeAllViews();

                        Collections.sort(skuDetailsList, new Comparator<SkuDetails>() {
                            @Override
                            public int compare(SkuDetails d1, SkuDetails d2) {
                                return (int) (d1.getPriceAmountMicros() - d2.getPriceAmountMicros());
                            }
                        });

                        for (final SkuDetails skuDetail : skuDetailsList) {
                            Button btn= new Button(getApplicationContext());
                            btn.setText(skuDetail.getDescription());
                            btn.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    comprar(skuDetail);
                                }
                            });
//                            btnWord[i].setHeight(50);
//                            btnWord[i].setWidth(50);
//                            btnWord[i].setTag(i);
                            //btnWord[i].setOnClickListener(btnClicked);
                            donateList.addView(btn);
                        }
                        showMainView();

                    }
                });
    }



    private void comprar(SkuDetails skuDetails){
        //inicializar flujo de compra
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        int responseCode = billingClient.launchBillingFlow(this, billingFlowParams).getResponseCode();

        // Handle the result.
    }





    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };




    void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.
        //Purchase purchase = ...;

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }


}
