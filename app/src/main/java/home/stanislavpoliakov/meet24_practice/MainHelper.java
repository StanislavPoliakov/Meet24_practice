package home.stanislavpoliakov.meet24_practice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс инициализации начальных значений
 * Здесь мы определяем список адресов картинок, формируем Url и загружаем данные
 */
public class MainHelper {
    private static final String TAG = "meet24_logs";
    private Set<String> resourceSet;

    /**
     * Основной метод для получения коллекции картинок
     * @return коллекция картинок, загруженных из сети
     */
    public Set<Bitmap> getCollection() {

        // Инициализируем множество адресов
        initResourceSet();

        // В Stream'-е коллекции трансформируем адрес в URL, загружаем картинку и сохраняем результат
        // в множестве картинок, которое и возвращаем
        return resourceSet.stream().parallel()
                .map(this::stringToUrl)
                .map(this::getBitmap)
                .collect(Collectors.toSet());
    }

    /**
     * Метод инициализации множества адресов.
     * На данном этапе адреса хранятся в строках (скопированных из адресной строки браузера)
     */
    private void initResourceSet() {
        resourceSet = new HashSet<>();
        resourceSet.add("https://images.pexels.com/photos/443446/pexels-photo-443446.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
        resourceSet.add("https://images.pexels.com/photos/8633/nature-tree-green-pine.jpg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/326055/pexels-photo-326055.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/462118/pexels-photo-462118.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/709552/pexels-photo-709552.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/589840/pexels-photo-589840.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/490411/pexels-photo-490411.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/1363876/pexels-photo-1363876.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/50594/sea-bay-waterfront-beach-50594.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/132037/pexels-photo-132037.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/1562/italian-landscape-mountains-nature.jpg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/46253/mt-fuji-sea-of-clouds-sunrise-46253.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/40896/larch-conifer-cone-branch-tree-40896.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/5969/wood-nature-forest-bridge.jpg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/355296/pexels-photo-355296.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/1300510/pexels-photo-1300510.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/547119/pexels-photo-547119.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/70741/cereals-field-ripe-poppy-70741.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/132419/pexels-photo-132419.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        resourceSet.add("https://images.pexels.com/photos/35627/shells-massage-therapy-sand.jpg?auto=compress&cs=tinysrgb&dpr=2&w=500");
    }

    /**
     * Метод конвертации адреса в формате String в адрес в формате URL
     * @param stringUrl адрес в формате String
     * @return адрес в формате URL
     */
    private URL stringToUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return url;
    }

    /**
     * Метод загрузки картинки из сети по данному url
     * @param url адрес картинки
     * @return картинка в формате Bitmap
     */
    private Bitmap getBitmap(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();

            //200 - OK
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Закрываем открытое соединение в блоке finally
            if (connection != null) connection.disconnect();
        }
        return null;
    }
}
