package taskDao.module.util;

import taskDao.module.dao.Game;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Диалоговое окно для редактирования существующей игры
 *
 * <p>Предоставляет пользовательский интерфейс для изменения данных
 * существующей игры с предзаполнением текущих значений.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class EditGameDialog extends BaseGameDialog {
    private final Game originalGame;

    /**
     * Конструктор создает диалог редактирования игры
     *
     * @param game игра для редактирования
     */
    public EditGameDialog(Game game) {
        super("Редактирование игры");
        this.originalGame = game;
        populateFields();
    }

    /**
     * Заполняет поля диалога данными из редактируемой игры
     */
    private void populateFields() {
        titleField.setText(originalGame.getTitle());
        genreComboBox.setValue(originalGame.getGenre());
        minSpinner.getValueFactory().setValue(originalGame.getMinPlayers());
        maxSpinner.getValueFactory().setValue(originalGame.getMaxPlayers());
    }

    /**
     * Настраивает валидацию полей для режима редактирования
     */
    @Override
    protected void setupValidation() {
        // Для редактирования все поля уже заполнены, поэтому кнопка активна
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(false);

        // Валидация все равно нужна на случай изменений
        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateFields(okButton));
        genreComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateFields(okButton));
    }

    /**
     * Отображает диалог и ожидает ввода пользователя
     *
     * @return Optional с отредактированной игрой если пользователь подтвердил изменения,
     *         или empty если операция отменена
     */
    public Optional<Game> showAndWait() {
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Game updatedGame = createGameFromFields(originalGame.getId());
                updatedGame.setLastPlayed(originalGame.getLastPlayed());
                updatedGame.setStatus(originalGame.getStatus()); // Сохраняем статус
                return updatedGame;
            }
            return null;
        });
        return dialog.showAndWait();
    }
}