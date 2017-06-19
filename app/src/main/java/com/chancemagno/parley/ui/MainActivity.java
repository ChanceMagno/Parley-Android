package com.chancemagno.parley.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.chancemagno.parley.R;
import com.chancemagno.parley.adapters.SectionsPageAdapter;
import com.chancemagno.parley.fragments.Fragment1;
import com.chancemagno.parley.fragments.Fragment3;

public class MainActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;


    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment1(), "Main");
        adapter.addFragment(new Fragment1(), "Event");
        adapter.addFragment(new Fragment3(), "Future Events");
        viewPager.setAdapter(adapter);
    }


}
