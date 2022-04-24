package kr.co.alphanewcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import kr.co.alphanewcare.Fragments.CareFragment_1;
import kr.co.alphanewcare.Fragments.CareFragment_2;


public class CareActivity extends BaseActivity{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String mCategoryCode;
    public static String mSubUrl_1 = "";
    public static String mSubUrl_2 = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care);
        init();
    }

    private void init()
    {
        Intent intent = getIntent();
        String topTitle = intent.getStringExtra("category");
        String subTitle_1 = intent.getStringExtra("title_1");
        String subTitle_2 = intent.getStringExtra("title_2");
        mCategoryCode = intent.getStringExtra("category_code");
        if(mCategoryCode.equals(MainActivity.TOOTH_LIST))
        {
            mSubUrl_1 = "https://blog.naver.com/alphado0313/221978918437";
            mSubUrl_2 = "https://blog.naver.com/alphado0313/221978922018";
        }else if(mCategoryCode.equals(MainActivity.SKIN_LIST))
        {
            mSubUrl_1 = "https://blog.naver.com/alphado0313/221978925083";
            mSubUrl_2 = "https://blog.naver.com/alphado0313/221978929019";
        }else if(mCategoryCode.equals(MainActivity.EAR_LIST))
        {
            mSubUrl_1 = "https://blog.naver.com/alphado0313/221978933390";
            mSubUrl_2 = "https://blog.naver.com/alphado0313/221978941318";
        }

        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.txtTitle)).setText(topTitle);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(subTitle_1));
        mTabLayout.addTab(mTabLayout.newTab().setText(subTitle_2));

        TabPagerAdapter pagerAdaper = new TabPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(pagerAdaper);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter{
        private int tabCount;
        public TabPagerAdapter(FragmentManager fm, int tabCount){
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    CareFragment_1 fragment_1 = new CareFragment_1();
                    return fragment_1;
                case 1:
                    CareFragment_2 fragment_2 = new CareFragment_2();
                    return fragment_2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
