package home.stanislavpoliakov.meet24_practice;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Класс трансформации слайдов при перелистовании
 */
public class AlphaPageTransformer implements ViewPager.PageTransformer {
    private static final String TAG = "meet24_logs";

    @Override
    public void transformPage(@NonNull View page, float position) {

        // Если положение View < -1, значит View находится слева и невидима
        if (position < -1) {
            page.setAlpha(0f);

            // Если в интервале [-1, 0], значит появляется слева (или скрывается слева)
        } else if (position < 0) {

            // Увеличиваем alpha в зависимости от позиции. Минимальное значение
            // поставил 20%, чтобы не совсем забеляло картинку
            page.setAlpha(Math.max(0.2f, (1 + position)));

            // Если в интервале [0, 1], значит появляется справа (или скрывается справа)
        } else if (position < 1) {

            // Увеличиваем alpha в зависимости от позиции. Минимальное значение
            // поставил 20%, чтобы не совсем забеляло картинку
            page.setAlpha(Math.max(0.2f, (1 - position)));

            // Если положение View > 1, значит View находится справа и невидима
        } else page.setAlpha(0f);
    }
}
