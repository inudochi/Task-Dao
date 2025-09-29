package taskDao.module.service;

import taskDao.module.dao.Game;
import taskDao.module.dao.GameDaoFactory;
import taskDao.module.managers.AlertManager;
import taskDao.module.managers.DataSourceManager;
import taskDao.module.managers.SessionDialogManager;
import taskDao.module.util.AddGameDialog;
import taskDao.module.util.EditGameDialog;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Основной сервисный класс приложения для управления бизнес-логикой
 *
 * Обеспечивает взаимодействие между UI-компонентами и сервисами данных,
 * инкапсулируя всю бизнес-логику приложения.
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class GameApplicationService {
    private final DataSourceManager dataSourceManager;
    private final AlertManager alertManager;
    private SessionDialogManager sessionManager;

    /**
     * Конструктор по умолчанию инициализирует менеджеры данных и уведомлений
     */
    public GameApplicationService() {
        this.dataSourceManager = new DataSourceManager();
        this.alertManager = new AlertManager();
    }

    // region Data Source Management

    /**
     * Переключает источник данных между JSON и PostgreSQL
     *
     * @param type тип источника данных для переключения
     * @throws RuntimeException если переключение не удалось
     */
    public void switchDataSource(GameDaoFactory.DataSourceType type) {
        try {
            info("Switching to data source: " + type);
            dataSourceManager.switchDataSource(type);
            initializeSessionManager();
            info("Successfully switched to: " + type);
        } catch (Exception e) {
            error("Error switching data source", e);
            throw new RuntimeException("Failed to switch to " + type, e);
        }
    }

    /**
     * Возвращает текстовую метку для отображения текущего источника данных
     *
     * @param type тип источника данных
     * @return строка с описанием источника данных
     */
    public String getDataSourceLabel(GameDaoFactory.DataSourceType type) {
        return "Source: " + type.name();
    }

    /**
     * Инициализирует менеджер сессий после смены источника данных
     */
    private void initializeSessionManager() {
        this.sessionManager = new SessionDialogManager(
                dataSourceManager.getGameService(),
                dataSourceManager.getValidationService()
        );
    }

    // endregion

    // region Game Management

    /**
     * Отображает диалог добавления новой игры
     *
     * @return Optional с новой игрой если пользователь подтвердил ввод,
     *         или empty если операция отменена
     */
    public Optional<Game> showAddGameDialog() {
        try {
            AddGameDialog dialog = new AddGameDialog();
            return dialog.showAndWait();
        } catch (Exception e) {
            error("Error opening add dialog", e);
            alertManager.showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to open add game dialog");
            return Optional.empty();
        }
    }

    /**
     * Проверяет корректность данных игры
     *
     * @param game игра для валидации
     * @return true если данные игры корректны, false в противном случае
     */
    public boolean validateGame(Game game) {
        return dataSourceManager.getValidationService().validateGameInput(
                game.getTitle(),
                game.getGenre(),
                game.getMinPlayers(),
                game.getMaxPlayers()
        );
    }

    /**
     * Добавляет новую игру в коллекцию
     *
     * @param game игра для добавления
     * @throws RuntimeException если добавление не удалось
     */
    public void addGame(Game game) {
        try {
            dataSourceManager.getGameService().addGame(game);
            info("Game added: " + game.getTitle());
        } catch (Exception e) {
            error("Error adding game", e);
            throw new RuntimeException("Failed to add game", e);
        }
    }

    /**
     * Отображает диалог редактирования существующей игры
     *
     * @param game игра для редактирования
     * @return Optional с отредактированной игрой если пользователь подтвердил изменения,
     *         или empty если операция отменена
     */
    public Optional<Game> showEditGameDialog(Game game) {
        try {
            EditGameDialog dialog = new EditGameDialog(game);
            return dialog.showAndWait();
        } catch (Exception e) {
            error("Error opening edit dialog", e);
            alertManager.showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to open edit dialog");
            return Optional.empty();
        }
    }

    /**
     * Обновляет данные существующей игры
     *
     * @param game игра с обновленными данными
     * @throws RuntimeException если обновление не удалось
     */
    public void updateGame(Game game) {
        try {
            dataSourceManager.getGameService().updateGame(game);
            info("Game updated: " + game.getTitle());
        } catch (Exception e) {
            error("Error updating game", e);
            throw new RuntimeException("Failed to update game", e);
        }
    }

    /**
     * Удаляет игру из коллекции
     *
     * @param game игра для удаления
     * @throws RuntimeException если удаление не удалось
     */
    public void deleteGame(Game game) {
        try {
            dataSourceManager.getGameService().deleteGame(game.getId());
            info("Game deleted: " + game.getTitle());
        } catch (Exception e) {
            error("Error deleting game", e);
            throw new RuntimeException("Failed to delete game", e);
        }
    }

    /**
     * Отображает диалог подтверждения удаления игры
     *
     * @param game игра для удаления
     * @return true если пользователь подтвердил удаление, false в противном случае
     */
    public boolean confirmGameDeletion(Game game) {
        return alertManager.showConfirmationDialog(
                "Confirm deletion",
                "Game deletion",
                "Are you sure you want to delete game: " + game.getTitle() + "?"
        );
    }

    // endregion

    // region Session Planning

    /**
     * Отображает диалог выбора даты для планирования игровой сессии
     *
     * @return Optional с выбранной датой если пользователь выбрал дату,
     *         или empty если операция отменена
     */
    public Optional<LocalDate> showSessionDatePicker() {
        try {
            return sessionManager.showDatePickerDialog();
        } catch (Exception e) {
            error("Error opening date picker dialog", e);
            return Optional.empty();
        }
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
            sessionManager.planGameSession(game, sessionDate);
            info("Session planned: " + game.getTitle() + " on " + sessionDate);
        } catch (Exception e) {
            error("Error planning session", e);
            throw new RuntimeException("Failed to plan session", e);
        }
    }

    /**
     * Проверяет корректность даты сессии
     *
     * @param date дата для проверки
     * @return true если дата корректна (не в прошлом), false в противном случае
     */
    public boolean validateSessionDate(LocalDate date) {
        return dataSourceManager.getValidationService().validateDate(date);
    }

    // endregion

    // region Game Data

    /**
     * Возвращает список всех игр из коллекции
     *
     * @return список всех игр
     * @throws RuntimeException если загрузка не удалась
     */
    public List<Game> getAllGames() {
        try {
            return dataSourceManager.getGameService().getAllGames();
        } catch (Exception e) {
            error("Error loading games", e);
            throw new RuntimeException("Failed to load games", e);
        }
    }

    /**
     * Возвращает случайную игру из коллекции
     *
     * <p>Предпочтение отдается играм, в которые не играли более 2 недель</p>
     *
     * @return случайная игра или null если коллекция пуста
     */
    public Game getRandomGame() {
        try {
            Game randomGame = dataSourceManager.getGameService().getRandomGame();
            if (randomGame == null) {
                warn("No games available for random selection");
            }
            return randomGame;
        } catch (Exception e) {
            error("Error selecting random game", e);
            return null;
        }
    }

    /**
     * Возвращает список одиночных игр (для 1 игрока)
     *
     * @return список одиночных игр
     */
    public List<Game> getSinglePlayerGames() {
        return dataSourceManager.getGameService().getSinglePlayerGames();
    }

    /**
     * Возвращает список многопользовательских игр (для 2+ игроков)
     *
     * @return список многопользовательских игр
     */
    public List<Game> getMultiplayerGames() {
        return dataSourceManager.getGameService().getMultiplayerGames();
    }

    /**
     * Обновляет статусы всех игр в коллекции
     *
     * <p>Игры, в которые не играли более 3 месяцев, помечаются как "Неактивно"</p>
     *
     * @throws RuntimeException если обновление не удалось
     */
    public void updateAllGamesStatus() {
        try {
            dataSourceManager.getGameService().updateAllGamesStatus();
            info("All game statuses updated");
        } catch (Exception e) {
            error("Error updating statuses", e);
            throw new RuntimeException("Failed to update game statuses", e);
        }
    }

    // endregion

    // region Utility Methods

    /**
     * Отображает информационное уведомление об успешной операции
     *
     * @param title заголовок уведомления
     * @param message текст сообщения
     */
    public void showSuccessAlert(String title, String message) {
        alertManager.showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Отображает уведомление об ошибке
     *
     * @param title заголовок ошибки
     * @param message текст сообщения об ошибке
     */
    public void showErrorAlert(String title, String message) {
        alertManager.showAlert(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Отображает уведомление со случайно выбранной игрой
     *
     * @param game случайно выбранная игра
     */
    public void showRandomGameAlert(Game game) {
        alertManager.showRandomGameAlert(game);
    }

    /**
     * Отображает стандартное уведомление об ошибке валидации
     */
    public void showValidationError() {
        alertManager.showValidationError();
    }

    /**
     * Возвращает сервис работы с играми
     *
     * @return экземпляр GameService
     */
    public GameService getGameService() {
        return dataSourceManager.getGameService();
    }

    // endregion
}