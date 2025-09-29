package taskDao.module.managers;

import taskDao.module.dao.Game;
import taskDao.module.service.GameService;
import taskDao.module.service.ValidationService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Менеджер для управления диалогами планирования игровых сессий
 *
 * <p>Обеспечивает взаимодействие с пользователем при планировании
 * игровых сессий и управлении датами игр.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class SessionDialogManager {
    private final GameService gameService;
    private final ValidationService validationService;

    /**
     * Конструктор инициализирует зависимости
     *
     * @param gameService сервис работы с играми
     * @param validationService сервис валидации данных
     */
    public SessionDialogManager(GameService gameService, ValidationService validationService) {
        this.gameService = gameService;
        this.validationService = validationService;
    }

    /**
     * Отображает диалог выбора даты для игровой сессии
     *
     * <p>Диалог позволяет выбрать только будущие даты, прошедшие даты блокируются</p>
     *
     * @return Optional с выбранной датой если пользователь подтвердил выбор,
     *         или empty если операция отменена
     */
    public Optional<LocalDate> showDatePickerDialog() {
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Планирование сессии");
        dialog.setHeaderText("Выберите дату для игровой сессии");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Дата сессии:"), 0, 0);
        grid.add(datePicker, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return datePicker.getValue();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Планирует игровую сессию для указанной игры
     *
     * @param game игра для которой планируется сессия
     * @param sessionDate дата планируемой сессии
     * @throws RuntimeException если планирование не удалось
     */
    public void planGameSession(Game game, LocalDate sessionDate) {
        try {
            game.logPlaySession();
            gameService.updateGame(game);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка планирования сессии для игры: " + game.getTitle(), e);
        }
    }
}