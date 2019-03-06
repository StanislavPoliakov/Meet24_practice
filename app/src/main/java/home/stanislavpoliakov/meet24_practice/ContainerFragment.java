package home.stanislavpoliakov.meet24_practice;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Класс фрагмента, в котором хранится наш ViewPager. Своего рода контейнер.
 * Появляется при нажатии на элемент RecyclerView
 */
public class ContainerFragment extends DialogFragment {
    private static final String TAG = "meet24_logs";
    private ViewPager viewPager;

    public static ContainerFragment newInstance() {
        return new ContainerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        initViewPager();
    }

    /**
     * Инициализируем ViewPager (меняем размер, устанавливаем адаптер и элемент отображения)
     */
    private void initViewPager() {
        // Меняем размер окна, в котором ViewPager будет отображать элементы
        // Получаем параметры ViewPager
        ViewGroup.LayoutParams lp = viewPager.getLayoutParams();

        // Получаем текущие параметры экрана
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);

        // Size.x = ширина экрана, Size.y = высота
        lp.width = size.x;

        // Высота установлена в 60% от ширины. Дело в том, что я знаю, что высота изображения
        // приблизительно равна 60% от ширины, поэтому устанавливаю такие же значения для
        // области отрисовки. Остальные настройки в самом фрагменте отображения.
        lp.height = (int) (size.x * 0.6);

        // Устанавливаем новые значения
        viewPager.setLayoutParams(lp);

        // Устанавливаем значение расстояния между слайдами
        viewPager.setPageMargin(8);

        // Получаем значение текущий позиции из аргументов фрагмета. Это номер элемента в коллекции
        // данных = номер элемента в списке RecyclerView = номер слайда в ViewPager
        Bundle bundle = getArguments();
        int currentPosition = bundle.getInt("position");

        // В адаптер передаем ChildFragmentManager, потому что контейнер для ViewPager у нас не
        // Activity, а фрагмент.
        // Полученные в этом фрагменте аргументы передаем в адаптер для отрисовки
        MyViewPagerAdapter mPagerAdapter = new MyViewPagerAdapter(bundle, getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        // Устанавливаем текущий слайд
        viewPager.setCurrentItem(currentPosition, true);
    }
}
