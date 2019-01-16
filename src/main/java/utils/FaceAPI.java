package utils;

import com.google.gson.*;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.DetectModel;
import models.VerifyModel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;

/**
 * Класс для распознавания/сравнения лиц. Используется Microsoft Cognitive Services Face API.
 */
public class FaceAPI {

    /**
     * Ключ подписки.
     */
    private static final String SUBSCRIPTION_KEY
            = "30f239c34bba4af8bb0205fa5bd1caa8";

    /**
     * URL для получения информации о лице по фото.
     */
    private static final String DETECT_URL = "/detect";

    /**
     * URL для сравнения лиц.
     */
    private static final String VERIFY_URL = "/verify";

    /**
     * Получение информации о лице на фото.
     * @param baseURL базовый URL для запроса
     * @param file фото лица
     * @return DetectModel
     * @throws URISyntaxException в случае неверного URL
     * @throws IOException в случае ошибки в запросе
     */
    public static DetectModel faceDetect(String baseURL, File file)
            throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(String.format("%s%s",
                baseURL, DETECT_URL));

        //параметры для запроса (необязательные)
        builder.setParameter("returnFaceId", "true");
        builder.setParameter("returnFaceLandmarks", "true");

        URI uri = builder.build();
        HttpPost request = new HttpPost(uri);

        //заголовки запроса
        request.setHeader("Content-Type", "application/octet-stream");
        request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

        //тело запроса
        FileEntity entity = new FileEntity(file);
        request.setEntity(entity);

        HttpResponse response
                = HttpClientBuilder.create().build().execute(request);
        HttpEntity responseBody = response.getEntity();
        DetectModel detect = null;
        String jsonString = null;

        //проверка статуса ответа и сбор объекта DetectModel
        if (response.getStatusLine().getStatusCode() == 200
                && responseBody != null && (jsonString = EntityUtils.toString(responseBody).trim()).length() > 2) {

            JsonParser jsonParser = new JsonParser();
            JsonArray jsonRes = (JsonArray)jsonParser.parse(jsonString);
            detect = new DetectModel(
                    jsonRes.get(0).getAsJsonObject()
                            .get("faceId").getAsString(),
                    jsonRes.get(0).getAsJsonObject()
                            .getAsJsonObject("faceRectangle"));
        }
        return detect;
    }

    /**
     * Сравнение лиц на двух фото.
     * @param baseURL базовый URL для запроса
     * @param face1 информация о лице 1
     * @param face2 информация о лице 2
     * @return результат сравнения лиц
     * @throws URISyntaxException в случае неверного URL
     * @throws IOException в случае ошибки в запросе
     */
    public static VerifyModel faceVerify(String baseURL,
                                         DetectModel face1,
                                         DetectModel face2)
            throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(String.format("%s%s", baseURL, VERIFY_URL));

        URI uri = builder.build();
        HttpPost request = new HttpPost(uri);

        //заголовки
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

        //тело запроса
        JsonObject faces = new JsonObject();
        faces.addProperty("faceId1", face1.getFaceId());
        faces.addProperty("faceId2", face2.getFaceId());

        StringEntity reqEntity = new StringEntity(faces.toString());
        request.setEntity(reqEntity);

        HttpResponse response
                = HttpClientBuilder.create().build().execute(request);
        HttpEntity responseBody = response.getEntity();
        VerifyModel verifyModel = null;

        //проверка статуса ответа и сбор объекта VerifyModel
        if (response.getStatusLine().getStatusCode() == 200
                && responseBody != null) {
            String jsonString = EntityUtils.toString(responseBody).trim();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonRes = (JsonObject)jsonParser.parse(jsonString);

            verifyModel = new VerifyModel(
                    jsonRes.get("isIdentical").getAsBoolean(),
                    jsonRes.get("confidence").getAsDouble());
        }
        return verifyModel;
    }

}
