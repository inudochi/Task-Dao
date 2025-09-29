package taskDao.module.managers;

import taskDao.module.dao.Game;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Менеджер для управления системными уведомлениями и диалоговыми окнами
 *
 * <p>Обеспечивает единообразное отображение различных типов уведомлений
 * в пользовательском интерфейсе приложения.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class AlertManager {

    /**
     * Отображает уведомление указанного типа
     *
     * @param alertType тип уведомления (INFORMATION, WARNING, ERROR и т.д.)
     * @param title заголовок уведомления
     * @param message текст сообщения
     */
    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Отображает стандартное уведомление об ошибке валидации данных
     *
     * <p>Используется при некорректном вводе данных в формах добавления/редактирования игр</p>
     */
    public void showValidationError() {
        showAlert(Alert.AlertType.ERROR, "Ошибка валидации",
                "Некорректные данные. Проверьте:\n- Название игры\n- Жанр из списка\n- Корректное количество игроков");
    }

    /**
     * Отображает уведомление со случайно выбранной игрой
     *
     * @param game случайно выбранная игра для отображения
     */
    public void showRandomGameAlert(Game game) {
        String content = String.format(
                "%s\nЖанр: %s\nИгроков: %d-%d\nСтатус: %s",
                game.getTitle(), game.getGenre(),
                game.getMinPlayers(), game.getMaxPlayers(),
                game.getStatus()
        );
        showAlert(Alert.AlertType.INFORMATION, "Случайная игра", content);
    }

    /**
     * Отображает диалог подтверждения действия
     *
     * @param title заголовок диалога
     * @param header заголовок содержимого
     * @param content текст сообщения
     * @return true если пользователь подтвердил действие, false в противном случае
     */
    public boolean showConfirmationDialog(String title, String header, String content) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(title);
        confirmation.setHeaderText(header);
        confirmation.setContentText(content);

        Optional<ButtonType> result = confirmation.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}