package home.stanislavpoliakov.meet24_practice;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    /**
     * Класс задачи для основной работы приложения! Получение данных из сети, сохранение и загрузка
     * коллекции изображений в/из галлереи устройства
     */

    // В качестве конечного результата получаем Map с изображениями в качестве ключей и с Uri этих
    // изображений в качестве значений. Эти Uri используются для последующей загрузки из галлереи в
    // слайдах ViewPager, а так как Bitmap во фрагмент мы передать не можем - передаем его Uri
    private class GetSaveAndLoadTask extends AsyncTask<Void, Void, Map<Bitmap, Uri>> {

        /**
         * Перед загрузкой осовной работой покзываем ProgressBar
         */
        @Override @UiThread
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Метод асинхронной работы
         * @param voids ничего не принимает
         * @return результат работы
         */
        @Override @WorkerThread
        protected Map<Bitmap, Uri> doInBackground(Void... voids) {

            // Очищаем галлерею, чтобы не переполнять устройство загрузкой. Поскольку работаем на
            // эмуляторе - очищаем всю. Конечно, на реальном устройстве очистка была бы выборочной
            clearStorage();

            // Получаем Stream коллекции и сохраняем каждый элемент в галлерее
            helper.getCollection().forEach(MainActivity.this::saveBitmap);

            // Загружаем данные из галлереи и возращаем
            return loadCollection();
        }

        /**
         * После окончания раоты прячем ProgressBar и инициализируем переменные
         * @param bitmaps
         */
        @Override @UiThread
        protected void onPostExecute(Map<Bitmap, Uri> bitmaps) {
            super.onPostExecute(bitmaps);
            progressBar.setVisibility(View.GONE);

            // Сохраняем результат работы
            data = bitmaps;

            // Инициализируем адаптер RecyclerView коллекцией изображений
            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(MainActivity.this,
                    new ArrayList<>(data.keySet()));

            // Отображать будем сеткой в 4 колонки
            GridLayoutManager layoutManager =
                    new GridLayoutManager(MainActivity.this, 4,
                            GridLayoutManager.VERTICAL, false);

            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initCheckPermissions();
        init();
        recyclerView = findViewById(R.id.recyclerView);
    }

    /**
     * Проверяем разрешения на запись во внешнем хранилище.
     * Наше приложения использует три вида разрешений: INTERNET, READ_EXTERNAL_STORAGE и
     * WRITE_EXTERNAL_STORAGE. INTERNET-разрешение имеет уровень PERMISSION_NORMAL, а значит
     * разрешение будет получено в момент установки приложения, если это разрешение объявлено в
     * манифесте приложения (то есть отдельно проверять его не нужно). Два других разрешения
     * имеют уровень PERMISSION_DANGEROUS, поскольку пытаются получить доступ к внутренней памяти
     * устройства и, возможно, к данным пользователя. Чтобы получить эти разрешения нужно, помимо
     * объявления в файле манифеста, дополнительно проверить их наличие и явно предупредить
     * пользователя о выдаче этих разрешений
     */
    private void initCheckPermissions() {
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check == -1) requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (check == -1) requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    /**
     * Инициализируем ProgressBar на время "тяжелых операций" и запускаем эти операции
     */
    private void init() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Операции "Получить данные из сети", "Сохранить в галлерее устройства" и "Загрузить из
        // галлереи устройства" проводим в отдельном AsyncTask
        GetSaveAndLoadTask task = new GetSaveAndLoadTask();
        task.execute();
    }

    /**
     * Метод очистки содержимого галлерии устройства
     *
     * Для целей обучения: Image Uri = content://media/external/images/media/2423
     */
    private void clearStorage() {

        // Определеяем инструмент доступа к внешнему контенту (или контенту контент-провайдера)
        ContentResolver contentResolver = getContentResolver();

        // Uri таблицы данных = content://media/external/images/media
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Проекция - это идентификатор ресурса в обобщенном виде
        // То есть для наших картинок - это 2423, 2424, 2425... и так далее. То есть то значение,
        // которое определяет наш конечный ресурс. Проекция определяет это значение, как переменную.
        String[] projection = new String[] {MediaStore.Images.Media._ID};

        // Получаем Cursor на все (where = null) данные из таблицы "content://media/external/images/media".
        // Переменная ресурсов = _id
        Cursor cursor = MediaStore.Images.Media.query(contentResolver, uri, projection, null, null);

        // Перемещаемся к первому элементу найденных значений
        cursor.moveToFirst();

        // Пока не прошли все значения... т.е. курсор не указывает за последний элемент (как Iterator)
        while (!cursor.isAfterLast()) {

            // Получаем ID изображения, считанное из единственной колонки с наименованием "_id"
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(projection[0]));

            // Получаем Uri ресурса, который необходимо удалить, путем "склеивания" путей
            // "content://media/external/images/media" + "/" + "2324"
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            // Удаляем ресурс из области контента
            contentResolver.delete(deleteUri, null, null);

            // Перемещаемся к следующему элементу
            cursor.moveToNext();
        }

        // Не забываем закрвать курсор
        cursor.close();
    }

    /**
     * Метод сохранения загруженной картинки в формате Bitmap в галлерею устройства
     * @param bitmap загруженная картинка
     */
    private void saveBitmap(Bitmap bitmap) {
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                "Bitmap", "Bitmap image #" + count);
    }

    /**
     * Метод получения коллекции картинок из галлерии устройства.
     * Надо сказать, что мы получаем содержимое всей галлерии. Поскольку мы ее всю очищаем перед
     * сохранением, то получается, что у нас всегда свежие (только что загруженные) картинки
     * @return коллекция картинок
     */
    private Map<Bitmap, Uri> loadCollection() {

        // Возвращать будем Map < Картинка, Адрес ресурса >
        Map<Bitmap, Uri> bitmapCollection = new HashMap<>();

        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {MediaStore.Images.Media._ID};
        Cursor cursor = MediaStore.Images.Media.query(contentResolver, uri, projection, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(projection[0]));
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            try {
                // Получаем картинку по идентификатору ресурса
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Сохраняем значения в коллекцию
                bitmapCollection.put(bitmap, imageUri);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            cursor.moveToNext();
        }
        cursor.close();

        // Возвращаем полученную коллекцию
        return bitmapCollection;
    }

    /**
     * Callback-метод обработки нажатия на элемент RecyclerView
     * @param itemPosition позиция элемента в списке
     */
    @Override
    public void itemClicked(int itemPosition) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Получаем экземпляр контейнера
        ContainerFragment fragment = ContainerFragment.newInstance();

        // Формируем Bundle, внутри которого будем передавать коллекцию идентификаторов
        // ресурсов и позицию элемента для текущего отображения
        Bundle bundle = new Bundle();

        // Получаем список строк через Stream и uri.toString()
        ArrayList<String> uriList = data.values().stream()
                .map(Objects::toString)
                .collect(Collectors.toCollection(ArrayList::new));

        // Помещаем список строк в Bundle
        bundle.putStringArrayList("uris", uriList);

        // И позицию элемента, который необходимо отобразить
        bundle.putInt("position", itemPosition);

        // Устанавливаем аргументы фрагмента-контейнера
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .add(fragment, "view pager")
                .commitNow();
    }
}
