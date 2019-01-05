package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import models.DetectModel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Первый контроллер.
 * Отвечает за загрузку, отображение, получение информации по первому изображению.
 */
public class FirstController extends BaseController {

    /**
     * Border Pane для первого изображения.
     */
    @FXML
    private BorderPane firstImagePane;

    /**
     * ImageView для первого изображения.
     */
    @FXML
    private ImageView firstImageView = new ImageView();

    /**
     * Кнопка перехода на следующую сцену.
     */
    @FXML
    private Button nextButton;

    /**
     * Информация о лице на фото.
     */
    private DetectModel detectModel;

    /**
     * Справочник для информации на фото.
     */
    private HashMap<String, DetectModel> detects = new HashMap<>();

    /**
     * Справочник для загруженных изображений.
     */
    private HashMap<String, ImageView> views = new HashMap<>();

    /**
     * Название ресурса для следующей сцены.
     */
    private final String NEXT_SCENE = "/view/SecondView.fxml";

    /**
     * Инициализация элементов на сцене.
     */
    @FXML
    private void initialize() {

        firstImagePane.heightProperty()
                .addListener((observable, oldValue, newValue)
                        -> firstImageView.setFitHeight((double) newValue));

        firstImagePane.widthProperty()
                .addListener((observable, oldValue, newValue)
                -> firstImageView.setFitWidth((double) newValue));

        if (detectModel == null) {
            nextButton.setDisable(true);
        }
    }

    /**
     * Установка переданных из другого контроллера detects и views.
     * @param detects справочник для информации на фото
     * @param views справочник для загруженных изображений
     */
    public void setDetectAndImageView(HashMap<String, DetectModel> detects,
                                      HashMap<String, ImageView> views) {
        this.detects = detects;
        this.views = views;

        detectModel = detects.get("first");
        firstImageView.setImage(views.get("first").getImage());
        nextButton.setDisable(false);
    }

    /**
     * Загрузка первого фото.
     * @throws URISyntaxException в случае неверного URL
     * @throws IOException в случае ошибки в запросе
     */
    public void onLoadFirstImage() throws URISyntaxException, IOException {
        detectModel = loadAndShowImage(firstImagePane, firstImageView);
        if (detectModel != null) {
            nextButton.setDisable(false);
        }
        detects.put("first", detectModel);
        views.put("first", firstImageView);
    }

    /**
     * Переход на следующую сцену.
     */
    public void onSecondSceneButton() {
        fadeOut(firstImagePane, NEXT_SCENE, detects, views);
    }




}
