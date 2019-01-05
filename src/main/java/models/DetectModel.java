package models;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-модель для представления информации о лице на фото.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectModel {

    private String faceId;
    private int x;
    private int y;
    private int width;
    private int height;

    public DetectModel(String faceId, JsonObject faceRectangle) {
        this.faceId = faceId;
        this.x = faceRectangle.get("left").getAsInt();
        this.y = faceRectangle.get("top").getAsInt();

        this.width = faceRectangle.get("width").getAsInt();
        this.height = faceRectangle.get("height").getAsInt();
    }

}
