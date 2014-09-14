package com.lock.rodrigo.lock;

import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lock.rodrigo.lock.UI.pageIndicator.CirclePageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class InstructionsActivity extends ActionBarActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;


    @InjectView(R.id.pager) ViewPager mViewPager;
    @InjectView(R.id.indicator) CirclePageIndicator indicator;
    @InjectView(R.id.frameindicator) FrameLayout frameIndicator;
    @InjectView(R.id.entendido) View entendido;
    @InjectView(R.id.saltar) View saltar;
    @InjectView(R.id.next)    View siguiente;

    static int screenWidth;
    static int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        ButterKnife.inject(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(new cambioDeFrame());
        regularBotonoesAbajo(mViewPager.getCurrentItem());
        //mViewPager.setPageTransformer(true, new DepthPageTransformer());


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

    }

    @OnClick(R.id.next)
    public void next(){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }


    @OnClick(R.id.saltar)
    public void salir1(){
       finish();
    }

    @OnClick(R.id.entendido)
    public void salir2(){
        finish();
    }


    public class cambioDeFrame extends ViewPager.SimpleOnPageChangeListener {
        private int currentPage;

        @Override
        public void onPageSelected(int position) {
           regularBotonoesAbajo(position);
            currentPage = position;
        }

        public final int getCurrentPage() {
            return currentPage;
        }
    }


    public void regularBotonoesAbajo(int position){
        if (position == mViewPager.getAdapter().getCount() -1 ){
            indicator.setVisibility(View.GONE);
            entendido.setVisibility(View.VISIBLE);
            saltar.setVisibility(View.GONE);
            siguiente.setVisibility(View.GONE);

        }else if (position == 0 ){
            saltar.setVisibility(View.VISIBLE);
            entendido.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
            siguiente.setVisibility(View.VISIBLE);

        }else{
            saltar.setVisibility(View.GONE);
            entendido.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
            siguiente.setVisibility(View.VISIBLE);
        }
    }





    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
     public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Home f=  new Home();
            Bundle args = new Bundle();
            args.putInt("idF", position);
            f.setArguments(args);
            return f;
           // return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }


    }




    static public class Home extends Fragment{
        @InjectView(R.id.titulo) TextView titulo;
        @InjectView(R.id.desc) TextView desc;
        @InjectView(R.id.imageView)  ImageView immagen;

        public Home() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int idF=getArguments().getInt("idF");
            View rootView = null;

            if (idF == 0){
                return inflater.inflate(R.layout.fragment_inst_splash, container, false);
            }else{
                rootView = inflater.inflate(R.layout.fragment_inst, container, false);
                ButterKnife.inject(this, rootView);

                switch (idF) {
                    case 1:
                        titulo.setText(R.string.intro_title1);
                        desc.setText(R.string.intro_desc1);

                        immagen.setImageResource(R.drawable.home1);
                        immagen.getLayoutParams().height = (int) (screenHeight * 0.60);
                        immagen.requestLayout();
                        break;

                    case 2:
                        titulo.setText(R.string.intro_title2);
                        desc.setText(R.string.intro_desc2);

                        immagen.setImageResource(R.drawable.home2);
                        immagen.getLayoutParams().height = (int) (screenHeight * 0.60);
                        immagen.requestLayout();
                        break;

                    case 3:
                        titulo.setText(R.string.intro_title3);
                        desc.setText(R.string.intro_desc3);

                        immagen.setImageResource(R.drawable.home3);
                        immagen.getLayoutParams().height = (int) (screenHeight * 0.60);
                        immagen.requestLayout();
                        break;

                   /* case 4:
                        titulo.setText(R.string.intro_title4);
                        ImageSpan is = new ImageSpan(getActivity(), R.drawable.extract_b);
                        SpannableString text = new SpannableString(getString(R.string.intro_desc4));
                        text.setSpan(is, 59, 59 + 1, 0);
                        desc.setText(text);


                        immagen.setImageResource(R.drawable.home4);
                        immagen.getLayoutParams().height = (int) (screenHeight * 0.50);
                        immagen.requestLayout();
                        break;*/
                }
            }

           return rootView;
        }

    }


}
