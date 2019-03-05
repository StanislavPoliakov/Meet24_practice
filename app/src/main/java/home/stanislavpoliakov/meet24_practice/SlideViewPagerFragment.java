package home.stanislavpoliakov.meet24_practice;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class SlideViewPagerFragment extends DialogFragment { private static final String TAG = "meet24_logs";
private Uri imageUri;
private ImageView pagerImageView;

    public static SlideViewPagerFragment newInstance() {
        Log.d(TAG, "newInstance: ");
        return new SlideViewPagerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_view_pager, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageUri = Uri.parse(getArguments().getString("image"));
        Log.d(TAG, "onViewCreated: imageUri = " + imageUri);

        pagerImageView = view.findViewById(R.id.pagerImageView);
        Log.d(TAG, "onViewCreated: ");
        setImage(view);
    }

    private void setImage(View view) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.width = bitmap.getWidth();
            lp.height = bitmap.getHeight();
            view.setLayoutParams(lp);
            pagerImageView.setImageBitmap(bitmap);
            pagerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
