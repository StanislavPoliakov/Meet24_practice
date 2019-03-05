package home.stanislavpoliakov.meet24_practice;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements ICallback{
    private static final String TAG = "meet24_logs";
    private Map<Bitmap, Uri> data;
    private MainHelper helper = new MainHelper();
    private int count = 0;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        recyclerView = findViewById(R.id.recyclerView);
    }

    /*private class NetworkTask extends AsyncTask<Void, Void, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(Void... voids) {
            return helper.getCollection();
        }

        @Override
        protected void onPostExecute(Set<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            resourceSet = bitmaps;
            //Log.d(TAG, "onPostExecute: size = " + bitmaps.size());
        }
    }*/

    private void init() {
        /*Log.d(TAG, "Permission INTERNET: " +
                ((ContextCompat.checkSelfPermission(this, INTERNET)
                        == PackageManager.PERMISSION_GRANTED) ? "GRANTED" : "DENIED"));
        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Log.d(TAG, "Permission READ_EXTERNAL_STORAGE: " +
                ((ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) ? "GRANTED" : "DENIED"));
        Log.d(TAG, "Permission WRITE_EXTERNAL_STORAGE: " +
                ((ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) ? "GRANTED" : "DENIED"));*/


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Runnable loadCollection = () -> {


        };
        Thread t = new Thread(loadCollection);
        //t.start();

        GetSaveAndLoadTask task = new GetSaveAndLoadTask();
        task.execute();

        //LoadCollectionTask loadCollectionTask = new LoadCollectionTask();
        //loadCollectionTask.execute();
        //Log.d(TAG, "init: size = " + resourceSet.size());
        //Log.d(TAG, "init: " + loadCollection().size());
    }

    @Override
    public void itemClicked(int itemPosition) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ContainerFragment fragment = ContainerFragment.newInstance();
        Bundle bundle = new Bundle();
        ArrayList<String> uriList = data.values().stream()
                .map(Objects::toString)
                .collect(Collectors.toCollection(ArrayList::new));
        bundle.putStringArrayList("uris", uriList);
        Log.d(TAG, "itemClicked: position = " + itemPosition);
        bundle.putInt("position", itemPosition);
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .add(fragment, "view pager")
                .commitNow();
    }

    private class GetSaveAndLoadTask extends AsyncTask<Void, Void, Map<Bitmap, Uri>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Map<Bitmap, Uri> doInBackground(Void... voids) {
            clearStorage();
            helper.getCollection().forEach(MainActivity.this::saveBitmap);
            return loadCollection();
        }

        @Override
        protected void onPostExecute(Map<Bitmap, Uri> bitmaps) {
            super.onPostExecute(bitmaps);
            progressBar.setVisibility(View.GONE);
            data = bitmaps;
            Log.d(TAG, "onPostExecute: data.size = " + data.size());
            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(MainActivity.this,
                    new ArrayList<>(data.keySet()));
            GridLayoutManager layoutManager =
                    new GridLayoutManager(MainActivity.this, 4,
                            GridLayoutManager.VERTICAL, false);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    private void saveBitmap(Bitmap bitmap) {
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                "Bitmap", "Bitmap image #" + count);
    }


    private Map<Bitmap, Uri> loadCollection() {

       Map<Bitmap, Uri> bitmapCollection = new HashMap<>();

        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //Log.d(TAG, "loadBitmap: uri = " + uri);

        String[] projection = new String[] {MediaStore.Images.Media._ID};
        //Log.d(TAG, "loadBitmap: projection = " + projection[0]);
        //String selection = MediaStore.Images.Media.DATA + " = ?";
        //Log.d(TAG, "loadBitmap: selection = " + selection);
        //String[] selectionArgs = new String[] { file.getAbsolutePath() };
        Cursor cursor = MediaStore.Images.Media.query(contentResolver, uri, projection, null, null);
        cursor.moveToFirst();
        //Log.d(TAG, "loadBitmap: cursor = " + cursor.getColumnNames()[0]);
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(projection[0]));
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            try {
                //Log.d(TAG, "load: imageUri = " + imageUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //Log.d(TAG, "Bitmap: " + bitmap.getWidth() + " x " + bitmap.getHeight());
                //Log.d(TAG, "loadCollection: bitmap = " + bitmap);
                bitmapCollection.put(bitmap, imageUri);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //Log.d(TAG, "loadBitmap: id = " + id);
            cursor.moveToNext();

        }
        cursor.close();
        return bitmapCollection;
    }

    private void clearStorage() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //Log.d(TAG, "loadBitmap: uri = " + uri);
        String[] projection = new String[] {MediaStore.Images.Media._ID};
        //Log.d(TAG, "loadBitmap: projection = " + projection[0]);
        //String selection = MediaStore.Images.Media.DATA + " = ?";
        //Log.d(TAG, "loadBitmap: selection = " + selection);
        //String[] selectionArgs = new String[] { file.getAbsolutePath() };
        Cursor cursor = MediaStore.Images.Media.query(contentResolver, uri, projection, null, null);
        cursor.moveToFirst();
        //Log.d(TAG, "loadBitmap: cursor = " + cursor.getColumnNames()[0]);
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(projection[0]));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
           // Log.d(TAG, "loadBitmap: id = " + id);
            cursor.moveToNext();
        }
        cursor.close();
    }


}
