package home.stanislavpoliakov.meet24_practice;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;
import java.util.stream.Collectors;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "meet22_logs";
    private List<Uri> data;
    private int currentPosition;

    public MyViewPagerAdapter(Bundle bundle, FragmentManager fm) {
        super(fm);
        Log.d(TAG, "MyViewPagerAdapter: ");
        this.currentPosition = bundle.getInt("position");
        data = bundle.getStringArrayList("uris").stream()
                .map(Uri::parse)
                .collect(Collectors.toList());
        //getItem(currentPosition);
    }

    @Override
    public Fragment getItem(int position) {
        SlideViewPagerFragment fragment = new SlideViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("image", String.valueOf(data.get(position)));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
