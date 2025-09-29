package taskDao.module.util;

import taskDao.module.dao.Game;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Диалоговое окно для добавления новой игры в коллекцию
 *
 * <p>Предоставляет пользовательский интерфейс для ввода данных
 * о новой игре с валидацией в реальном времени.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class AddGameDialog extends BaseGameDialog {

    /**
     * Конструктор создает диалог добавления игры
     */
    public AddGameDialog() {
        super("Добавить новую игру");
    }

    /**
     * Отображает диалог и ожидает ввода пользователя
     *
     * @return Optional с новой игрой если пользователь подтвердил ввод,
     *         или empty если операция отменена
     */
    public Optional<Game> showAndWait() {
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return createGameFromFields(0); // ID будет установлен при добавлении
            }
            return null;
        });
        return dialog.showAndWait();
    }
}