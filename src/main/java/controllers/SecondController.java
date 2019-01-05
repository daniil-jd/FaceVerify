package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import models.DetectModel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Второй контроллер.
 * Отвечает за загрузку, отображение, получение информации по второму изображению.
 */
public class SecondController extends BaseController {

    /**
     * Border Pane для второго изображения.
     */
    @FXML
    private BorderPane secondImagePane;

    /**
     * ImageView для второго изображения.
     */
    @FXML
    private ImageView secondImageView = new ImageView();

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
    private final String NEXT_SCENE = "/view/ThirdView.fxml";

    /**
     * Название ресурса для предыдущей сцены.
     */
    private final String PREVIOUS_SCENE = "/view/FirstView.fxml";

    /**
     * Инициализация элементов на сцене.
     */
    @FXML
    private void initialize() {

        secondImagePane.heightProperty()
                .addListener((observable, oldValue, newValue)
                        -> secondImageView.setFitHeight((double) newValue));

        secondImagePane.widthProperty()
                .addListener((observable, oldValue, newValue)
                        -> secondImageView.setFitWidth((double) newValue));

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

        if (detects.get("second") != null) {
            detectModel = detects.get("second");
            secondImageView.setImage(views.get("second").getImage());

            nextButton.setDisable(false);
        }
    }

    /**
     * Загрузка второго фото.
     * @throws URISyntaxException в случае неверного URL
     * @throws IOException в случае ошибки в запросе
     */
    public void onLoadSecondImage()
            throws URISyntaxException, IOException {
        detectModel = loadAndShowImage(secondImagePane, secondImageView);
        if (detectModel != null) {
            nextButton.setDisable(false);
        }

        detects.put("second", detectModel);
        views.put("second", secondImageView);
    }

    /**
     * Переход на следующую сцену.
     */
    public void onThirdSceneButton() {
        fadeOut(secondImagePane, NEXT_SCENE, detects, views);
    }

    /**
     * Переход на предыдущую сцену.
     */
    public void onFirstSceneButton() {
        fadeOut(secondImagePane, PREVIOUS_SCENE, detects, views);
    }
}
