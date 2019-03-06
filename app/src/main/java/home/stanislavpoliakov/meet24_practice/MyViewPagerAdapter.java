package home.stanislavpoliakov.meet24_practice;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс адаптера для ViewPager
 */
public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "meet22_logs";
    private List<Uri> data;

    public MyViewPagerAdapter(Bundle bundle, FragmentManager fm) {
        super(fm);

        // В качестве данных для отображения используем коллекцию
        // идентификаторов ресурсов.
        // В Stream собираем Uri из String и сохраняем в список, который
        // и будем отображать
        data = bundle.getStringArrayList("uris").stream()
                .map(Uri::parse)
                .collect(Collectors.toList());
    }

    /**
     * Метод получения слайда по позиции
     * @param position позиция в коллекции слайдов
     * @return слайд-фрагмент
     */
    @Override
    public Fragment getItem(int position) {

        // Получаем экземпляр слайда
        SlideViewPagerFragment fragment = SlideViewPagerFragment.newInstance();

        // Формируем данные, которые слайд будет отображать и устанавливаем их в качестве аргументов
        // слайда
        Bundle bundle = new Bundle();
        bundle.putString("image", String.valueOf(data.get(position)));
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Количество слайдов = количество ресурсов
     * @return размер
     */
    @Override
    public int getCount() {
        return data.size();
    }
}
