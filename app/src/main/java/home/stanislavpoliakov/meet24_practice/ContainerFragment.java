package home.stanislavpoliakov.meet24_practice;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContainerFragment extends DialogFragment {
    private static final String TAG = "meet24_logs";

    public static ContainerFragment newInstance() {
        return new ContainerFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    private int pxToDp(int px) {
        return (int) (px / ((float) getContext().getResources().getDisplayMetrics().scaledDensity));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.d(TAG, "onViewCreated: ");
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
        lp.width = pxToDp(1000);
        lp.height = pxToDp(650);
        viewPager.setLayoutParams(lp);


        Bundle bundle = getArguments();
        int currentPosition = bundle.getInt("position");

        MyViewPagerAdapter mPagerAdapter = new MyViewPagerAdapter(bundle, getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(currentPosition, true);
        //Log.d(TAG, "onViewCreated: ");

    }

    /*public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
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
        }

        @Override
        public Fragment getItem(int position) {
            SlideViewPagerFragment fragment = SlideViewPagerFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("image", String.valueOf(data.get(position)));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }*/

}
