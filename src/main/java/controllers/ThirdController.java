package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import models.DetectModel;
import models.VerifyModel;
import utils.FaceAPI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Третий контроллер.
 * Отвечает вывод результата сравнения двух изображений.
 */
public class ThirdController extends BaseController {

    /**
     * HBox для изображений.
     */
    @FXML
    private HBox thirdImageHBox;

    /**
     * ImageView для первого изображения.
     */
    @FXML
    private ImageView res1ImageView = new ImageView();

    /**
     * ImageView для второго изображения.
     */
    @FXML
    private ImageView res2ImageView = new ImageView();

    /**
     * Label для отображения результата сравнения.
     */
    @FXML
    private Label resultLabel;

    /**
     * Справочник для информации на фото.
     */
    private HashMap<String, DetectModel> detects = new HashMap<>();

    /**
     * Справочник для загруженных изображений.
     */
    private HashMap<String, ImageView> views = new HashMap<>();

    /**
     * Информация о лице на первом фото.
     */
    private DetectModel firstDetectModel;

    /**
     * Информация о лице на втором фото.
     */
    private DetectModel secondDetectModel;

    /**
     * Название ресурса для предыдущей сцены.
     */
    private final String PREVIOUS_SCENE = "/view/SecondView.fxml";

    /**
     * Инициализация элементов на сцене.
     */
    @FXML
    private void initialize() {

        thirdImageHBox.heightProperty()
                .addListener((observable, oldValue, newValue) -> {
            res1ImageView.setFitHeight((double) newValue);
            res1ImageView.setFitHeight((double) newValue);
        });

        thirdImageHBox.widthProperty()
                .addListener((observable, oldValue, newValue) -> {
            double half = (double) newValue / 2;
            res1ImageView.setFitWidth(half);
            res2ImageView.setFitWidth(half);
        });
    }

    /**
     * Переход на предыдущую сцену.
     */
    public void onSecondSceneButton() {
        fadeOut(thirdImageHBox, PREVIOUS_SCENE, detects, views);
    }

    /**
     * Установка переданных из другого контроллера detects и views.
     * @param detects справочник для информации на фото
     * @param views справочник для загруженных изображений
     */
    public void setDetectAndImageView(HashMap<String, DetectModel> detects, HashMap<String, ImageView> views)
            throws IOException, URISyntaxException {
        this.detects = detects;
        this.views = views;
        if (detects.get("first") != null) {
            firstDetectModel = detects.get("first");

            res1ImageView.setPreserveRatio(true);
            res1ImageView.setImage(views.get("first").getImage());
        }

        if (detects.get("second") != null) {
            secondDetectModel = detects.get("second");

            res2ImageView.setPreserveRatio(true);
            res2ImageView.setImage(views.get("second").getImage());
        }

        VerifyModel verifyModel = FaceAPI.faceVerify(getUriBase(),
                firstDetectModel, secondDetectModel);
        resultLabel.setText(verifyModel.toString());
    }
}
