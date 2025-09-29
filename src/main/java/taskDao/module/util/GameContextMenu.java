package taskDao.module.util;

import taskDao.module.dao.Game;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

import static taskDao.module.util.LoggerUtil.debug;
import static taskDao.module.util.LoggerUtil.error;

/**
 * Контекстное меню для таблицы игр
 *
 * <p>Предоставляет быстрый доступ к часто используемым операциям
 * через контекстное меню, вызываемое правой кнопкой мыши на таблице.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class GameContextMenu {
    private final ContextMenu contextMenu = new ContextMenu();
    private final TableView<Game> gameTable;

    // Колбэки для действий
    private final Runnable onPlanSession;
    private final Runnable onEditGame;
    private final Runnable onViewDetails;

    /**
     * Конструктор создает контекстное меню с указанными обработчиками
     *
     * @param gameTable таблица игр для привязки меню
     * @param onPlanSession обработчик планирования сессии
     * @param onEditGame обработчик редактирования игры
     * @param onViewDetails обработчик просмотра деталей игры
     */
    public GameContextMenu(TableView<Game> gameTable,
                           Runnable onPlanSession,
                           Runnable onEditGame,
                           Runnable onViewDetails) {
        this.gameTable = gameTable;
        this.onPlanSession = onPlanSession;
        this.onEditGame = onEditGame;
        this.onViewDetails = onViewDetails;
        setupMenu();
    }

    /**
     * Настраивает пункты контекстного меню
     */
    private void setupMenu() {
        MenuItem planSessionItem = new MenuItem("Запланировать сессию");
        planSessionItem.setOnAction(e -> handlePlanSession());

        MenuItem editItem = new MenuItem("Редактировать");
        editItem.setOnAction(e -> handleEditGame());

        MenuItem viewDetailsItem = new MenuItem("Просмотреть детали");
        viewDetailsItem.setOnAction(e -> handleViewDetails());

        contextMenu.getItems().addAll(planSessionItem, editItem, viewDetailsItem);

        // Отключаем пункты меню если нет выделения
        contextMenu.setOnShowing(e -> updateMenuItemsState());

        debug("Контекстное меню инициализировано");
    }

    /**
     * Обновляет состояние пунктов меню в зависимости от наличия выделения
     */
    private void updateMenuItemsState() {
        boolean hasSelection = getSelectedGame() != null;
        contextMenu.getItems().forEach(item -> item.setDisable(!hasSelection));
    }

    /**
     * Обрабатывает выбор пункта "Запланировать сессию"
     */
    private void handlePlanSession() {
        if (getSelectedGame() != null && onPlanSession != null) {
            debug("Контекстное меню: запланировать сессию");
            onPlanSession.run();
        }
    }

    /**
     * Обрабатывает выбор пункта "Редактировать"
     */
    private void handleEditGame() {
        if (getSelectedGame() != null && onEditGame != null) {
            debug("Контекстное меню: редактировать игру");
            onEditGame.run();
        }
    }

    /**
     * Обрабатывает выбор пункта "Просмотреть детали"
     */
    private void handleViewDetails() {
        Game selected = getSelectedGame();
        if (selected != null && onViewDetails != null) {
            debug("Контекстное меню: просмотреть детали игры: " + selected.getTitle());
            onViewDetails.run();
        }
    }

    /**
     * Возвращает выбранную в таблице игру
     *
     * @return выбранная игра или null если ничего не выбрано
     */
    private Game getSelectedGame() {
        return gameTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Отображает детальную информацию о выбранной игре
     *
     * @param game игра для отображения деталей
     */
    public void showGameDetails(Game game) {
        try {
            String details = String.format(
                    "Детали игры:\n\nНазвание: %s\nЖанр: %s\nИгроков: %d-%d\nСтатус: %s\nПоследняя игра: %s",
                    game.getTitle(),
                    game.getGenre(),
                    game.getMinPlayers(),
                    game.getMaxPlayers(),
                    game.getStatus(),
                    game.getLastPlayed() != null ?
                            game.getLastPlayed().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) :
                            "Никогда"
            );

            showInfoAlert("Детали игры", details);
        } catch (Exception e) {
            error("Ошибка отображения деталей игры", e);
            showErrorAlert("Ошибка", "Не удалось отобразить детали игры");
        }
    }

    /**
     * Отображает уведомление об ошибке
     *
     * @param title заголовок ошибки
     * @param message текст сообщения об ошибке
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Отображает информационное уведомление
     *
     * @param title заголовок уведомления
     * @param message текст сообщения
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Возвращает контекстное меню для привязки к таблице
     *
     * @return сконфигурированное контекстное меню
     */
    public ContextMenu getContextMenu() {
        return contextMenu;
    }
}