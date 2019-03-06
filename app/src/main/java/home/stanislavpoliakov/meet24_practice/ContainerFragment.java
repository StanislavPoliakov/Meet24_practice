package home.stanislavpoliakov.meet24_practice;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContainerFragment extends DialogFragment {
    private static final String TAG = "meet24_logs";
    private ViewPager viewPager;

    public static ContainerFragment newInstance() {
        return new ContainerFragment();
    }

    /*@Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_container2, container, false);
    }

    private int pxToDp(int px) {
        return (int) (px / ((float) getContext().getResources().getDisplayMetrics().scaledDensity));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.d(TAG, "onViewCreated: ");
        viewPager = view.findViewById(R.id.viewPager);
        ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        lp.width = size.x;
        lp.height = (int) (size.x * 0.6);
        viewPager.setLayoutParams(lp);
        viewPager.setPageMargin(0);


        Bundle bundle = getArguments();
        int currentPosition = bundle.getInt("position");

        MyViewPagerAdapter mPagerAdapter = new MyViewPagerAdapter(bundle, getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(currentPosition, true);
        //Log.d(TAG, "onViewCreated: ");

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                List<Uri> data = bundle.getStringArrayList("uris").stream()
                        .map(Uri::parse)
                        .collect(Collectors.toList());
                Uri imageUri = data.get(position);

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
                        lp.width = bitmap.getWidth();
                        lp.height = bitmap.getHeight();
                        viewPager.setLayoutParams(lp);

                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

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
