package home.stanislavpoliakov.meet24_practice;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Класс слайда
 */
public class SlideViewPagerFragment extends DialogFragment {
    private static final String TAG = "meet24_logs";
    private Uri imageUri;

    // Детектор жеста зума
    private ScaleGestureDetector mScaleGestureDetector;

    // Коэффициент увеличения
    private float mScaleFactor = 1.0f;
    private ImageView pagerImageView;

    public static SlideViewPagerFragment newInstance() {
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

    /**
     * После создания View инициализируем элементы
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Собираем из String идентификатор ресурса, который необходимо оторбразить в слайде
        imageUri = Uri.parse(getArguments().getString("image"));

        pagerImageView = view.findViewById(R.id.pagerImageView);

        // Устанавливаем изображение
        setImage();

        mScaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener());

        // Обрабатываем onTouch для картинки через ScaleGestureDetector, чтобы отловить жест зума
        pagerImageView.setOnTouchListener((v, event) -> mScaleGestureDetector.onTouchEvent(event));
    }

    /**
     * Метод установки изображения по ресурсу
     */
    private void setImage() {
        try {
            // Получаем картинку из галлерии устройств по идентификатору ресурса
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

            // Усталавливаем изображение
            pagerImageView.setImageBitmap(bitmap);

            // Центрируем
            pagerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Поскольку разные изображения имеют разные соотношения сторон - немного увеличиваем
            // изображение, чтобы не было видно белых кусков по сторонам. Надо сказать, что хватило
            // бы и 5% (1.05f), но одно изображение в коллекции уж очень выбивается из "нормы"
            pagerImageView.setScaleX(1.15f);
            pagerImageView.setScaleY(1.15f);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Класс, описывающий обработчик жеста зума
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Получаем в процессе зума коэфициент увеличения
            // Если увеличиваем изображение - getScaleFactor > 1,
            // если уменьшаем - < 1
            // Меняем общий коэффициент на полученный в результате жеста
            mScaleFactor *= mScaleGestureDetector.getScaleFactor();

            // Поскольку увелчение картинки у нас стоит 15% (1.15f), то и значения, которые
            // может принимать наш коэффициент ограничим снизу 15% (чтобы не отдалять за пределы
            // начального изображения), а сверху 200%
            mScaleFactor = Math.max(1.15f, Math.min(mScaleFactor, 2.0f));

            // Применяем коэффициент к изображению
            pagerImageView.setScaleX(mScaleFactor);
            pagerImageView.setScaleY(mScaleFactor);

            // Жест обработан (true)
            return true;
        }
    }
}
