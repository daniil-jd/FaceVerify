package controllers;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.DetectModel;
import utils.FaceAPI;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 * Базовый класс для контроллеров.
 */
public class BaseController {

    /**
     * Базовый URL для запросов к Face API.
     */
    private static final String uriBase =
            "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";


    public static String getUriBase() {
        return uriBase;
    }

    /**
     * Диалоговое окно для выбора фото в формате .jpg.
     * @return FileChooser
     * @throws URISyntaxException в случае неверного пути до директории
     */
    private FileChooser getFileChooser() throws URISyntaxException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберете изображение");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        return fileChooser;
    }

    /**
     * Загрузка и отображение фото.
     * @param pane панель компановки
     * @param imageView узел для отображения фото
     * @return DetectModel
     * @throws URISyntaxException в случае неверного URL
     * @throws IOException в случае ошибки в запросе
     */
    public DetectModel loadAndShowImage(Pane pane, ImageView imageView)
            throws URISyntaxException, IOException {
        File imageFile
                = getFileChooser().showOpenDialog(pane.getScene().getWindow());
        DetectModel detect = null;
        if (imageFile != null) {
            detect = FaceAPI.faceDetect(getUriBase(), imageFile);

            if (detect != null) {
                Image image = new Image(imageFile.toURI().toURL().toString());
                WritableImage writableImage = drawRectangleOnImage(image, detect);

                imageView.setPreserveRatio(true);
                imageView.setImage(writableImage);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка при загрузке изображения.");
                alert.setHeaderText("Ошибка при загрузке изображения.");
                alert.setContentText("Изображение не содержит лиц. " +
                        "Выберете другое изображение для загрузки.");

                alert.showAndWait();
            }
        }
        return detect;
    }

    /**
     * Отрисовка "прямоуголиника" с лицом на фото.
     * @param image изображение
     * @param detect информация о фото
     * @return изображение с "прямоугольником"
     */
    public WritableImage drawRectangleOnImage(Image image,
                                              DetectModel detect) {
        WritableImage wImage = new WritableImage((int)image.getWidth(),
                (int)image.getHeight());

        PixelReader pixelReader = image.getPixelReader();
        PixelWriter writer = wImage.getPixelWriter();
        //левая точка прямоуголника
        int xLeft = detect.getX();
        //правая точка прямоугольника
        int xRight = xLeft + detect.getWidth();
        //верхняя точка прямоугольника
        int yTop = detect.getY();
        //нижняя точка прямоугольника
        int yBottom = yTop + detect.getHeight();

        //проход по изображению и отрисовка прямоугольника
        for(int y = 0; y < (int)image.getHeight(); y++) {
            for(int x = 0; x < (int)image.getWidth(); x++) {
                if (x <= xRight && x >= xLeft && y >= yTop && y <= yTop + 1
                        || x <= xRight && x >= xLeft && y >= yBottom - 2 && y <= yBottom
                        || x >= xLeft && x <= xLeft + 1 && y >= yTop && y <= yBottom
                        || x >= xRight && x <= xRight + 1 && y >= yTop && y <= yBottom) {
                    writer.setColor(x, y, Color.RED);
                } else {
                    Color color = pixelReader.getColor(x, y);
                    writer.setColor(x, y, color);
                }
            }
        }
        return wImage;
    }

    /**
     * Анимация исчезновения для перехода между сценами.
     * @param node узел текущей сцены
     * @param resourceName название ресурса fxml следующей сцены
     * @param detects справочник объектов DetectModel
     * @param imageViews справочник объектов ImageView
     */
    public void fadeOut(Node node, String resourceName,
                        HashMap<String, DetectModel> detects,
                        HashMap<String, ImageView> imageViews) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        fadeTransition.setOnFinished(event -> {
            try {
                loadNextScreen((Stage) node.getScene().getWindow(),
                        resourceName, detects, imageViews);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

        fadeTransition.play();
    }

    /**
     * Загрузка следущей сцены.
     * @param currentStage текущая сцена
     * @param resourceName название ресурса fxml следующей сцены
     * @param detects справочник объектов DetectModel
     * @param imageViews справочник объектов ImageView
     * @throws URISyntaxException в случае неверного URL
     * @throws IOException в случае ошибки в запросе
     */
    private void loadNextScreen(Stage currentStage, String resourceName,
                                HashMap<String, DetectModel> detects,
                                HashMap<String, ImageView> imageViews)
            throws IOException, URISyntaxException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceName));
        Parent nextView = loader.load();
        Scene scene = new Scene(nextView);
        currentStage.setScene(scene);

        //из второго в первый контроллер
        if (resourceName.contains("FirstView")) {
            FirstController controller = loader.getController();
            controller.setDetectAndImageView(detects, imageViews);
            currentStage.setTitle("Первый экран");

        //из первого во второй контроллер или из третьего во второй
        } else if (resourceName.contains("SecondView")) {
            SecondController controller = loader.getController();
            controller.setDetectAndImageView(detects, imageViews);
            currentStage.setTitle("Второй экран");
        //из второго в третий контроллер
        } else  if(resourceName.contains("ThirdView")) {
            ThirdController controller = loader.getController();
            controller.setDetectAndImageView(detects, imageViews);
            currentStage.setTitle("Третий экран");
        }
        currentStage.show();
    }
}
