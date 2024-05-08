package com.koekoetech.clinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.UhcProgressNotePhotoDetailFragment;
import com.koekoetech.clinic.helper.HackyViewPager;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by ZMN on 8/29/17.
 **/

public class UhcProgressNotePhotoDetailActivity extends BaseActivity {

    private static final String EXTRA_PHOTOS = "photos";
    private static final String EXTRA_INIT_POS = "init_pos";

    private HackyViewPager photoDetailPager;

    private ArrayList<UhcPatientProgressNotePhoto> progressPhotosList;

    public static Intent getPhotoIntent(Context context, ArrayList<UhcPatientProgressNotePhoto> progressNotePhotos, int initPosition) {
        Intent intent = new Intent(context, UhcProgressNotePhotoDetailActivity.class);
        intent.putExtra(EXTRA_INIT_POS, initPosition);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_PHOTOS, progressNotePhotos);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_photo_detail;
    }


    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        photoDetailPager = findViewById(R.id.photo_detail_pager);

        int initPosition = getIntent().getIntExtra(EXTRA_INIT_POS, 0);
        progressPhotosList = getIntent().getParcelableArrayListExtra(EXTRA_PHOTOS);

        if (progressPhotosList == null || progressPhotosList.isEmpty()) {
            Toast.makeText(this, "No Photos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }


        setupToolbar(true);
        setupToolbarBgColor("#000000");

        setupPagerAdapter(initPosition);

    }

    private void setupPagerAdapter(int initPos) {
        photoDetailPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return UhcProgressNotePhotoDetailFragment.newInstance(progressPhotosList.get(position));
            }

            @Override
            public int getCount() {
                return progressPhotosList.size();
            }
        });

        photoDetailPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private boolean isFirst = true;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isFirst && positionOffset == 0 && positionOffsetPixels == 0) {
                    onPageSelected(position);
                    isFirst = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                changeToolbarTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        photoDetailPager.setCurrentItem(initPos);
    }

    private void changeToolbarTitle(int position) {
        if (position < progressPhotosList.size()) {
            UhcPatientProgressNotePhoto photoModel = progressPhotosList.get(position);
            setupToolbarText(photoModel.getType());
        } else {
            setupToolbarText("");
        }
    }

}
