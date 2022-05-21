package com.rp.toyexchanger.ui.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.ui.ui.MyOffers.MyOffersFragment;
import com.rp.toyexchanger.ui.ui.Offers.OffersFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.rp.toyexchanger.R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new OffersFragment(), "OFFERS");
        vpAdapter.addFragment(new MyOffersFragment(), "MY OFFERS");
        viewPager.setAdapter(vpAdapter);
    }
}