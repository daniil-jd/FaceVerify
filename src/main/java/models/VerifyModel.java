package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-модель для представления результата сравнения двух лиц.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyModel {

    private boolean isIdentical;
    private double confidence;

    @Override
    public String toString() {
        return String.format("%s. Процент совпадения: %.2f",
                isIdentical? "Лица одного человека" : "Лица разных людей",
                confidence);
    }
}
