package taskDao.main;

import taskDao.module.dao.Game;
import taskDao.module.dao.GameDaoFactory;
import taskDao.module.managers.TableFilterManager;
import taskDao.module.managers.TableManager;
import taskDao.module.managers.TooltipManager;
import taskDao.module.util.GameContextMenu;
import taskDao.module.service.GameApplicationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Главный контроллер приложения для управления игровой коллекцией
 *
 * <p>Координирует взаимодействие между пользовательским интерфейсом
 * и бизнес-логикой приложения, обрабатывает пользовательские действия
 * и обновляет отображение данных.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class GameController {
    // FXML компоненты
    @FXML private TableView<Game> gameTable;
    @FXML private MenuButton dataSourceMenu;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Label statusLabel;
    @FXML private Label itemsCountLabel;
    @FXML private Button addGameButton;
    @FXML private Button deleteGameButton;
    @FXML private Button randomGameButton;

    // Менеджеры и сервисы
    private GameApplicationService appService;
    private TableManager tableManager;
    private TableFilterManager filterManager;

    /**
     * Метод инициализации контроллера, вызываемый JavaFX
     *
     * <p>Настраивает все компоненты пользовательского интерфейса,
     * инициализирует сервисы и устанавливает обработчики событий.</p>
     */
    @FXML
    public void initialize() {
        try {
            info("Initializing GameController");

            initializeServicesAndManagers();
            setupTooltips();
            setupFilters();
            setupTableListeners();
            setupContextMenu();
            switchDataSource(GameDaoFactory.DataSourceType.JSON);

            info("Controller successfully initialized");
        } catch (Exception e) {
            error("Error initializing controller", e);
            showErrorAlert("Startup error", "Failed to initialize application");
        }
    }

    /**
     * Инициализирует сервисы и менеджеры приложения
     */
    private void initializeServicesAndManagers() {
        this.appService = new GameApplicationService();
        this.tableManager = new TableManager(gameTable);
    }

    /**
     * Настраивает всплывающие подсказки для элементов интерфейса
     */
    private void setupTooltips() {
        TooltipManager.setupGameControllerTooltips(
                gameTable,
                dataSourceMenu,
                filterComboBox,
                addGameButton,
                deleteGameButton,
                randomGameButton,
                statusLabel,
                itemsCountLabel
        );
    }

    /**
     * Настраивает систему фильтрации игр в таблице
     */
    private void setupFilters() {
        this.filterManager = new TableFilterManager(appService.getGameService());
        filterManager.setupFilterComboBox(filterComboBox);
        filterComboBox.setOnAction(e -> applyFilter());
    }

    /**
     * Настраивает обработчики событий для таблицы игр
     */
    private void setupTableListeners() {
        // Двойной клик для быстрого планирования сессии
        gameTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                handlePlanSession();
            }
        });
    }

    /**
     * Настраивает контекстное меню для таблицы игр
     */
    private GameContextMenu gameContextMenu;

    private void setupContextMenu() {
        this.gameContextMenu = new GameContextMenu(
                gameTable,
                this::handlePlanSession,
                this::handleEditGame,
                this::handleViewDetails
        );
        gameTable.setContextMenu(gameContextMenu.getContextMenu());
    }

    // region Data Source Management

    /**
     * Обрабатывает переключение на источник данных JSON
     */
    @FXML
    private void handleSwitchToJson() {
        switchDataSource(GameDaoFactory.DataSourceType.JSON);
    }

    /**
     * Обрабатывает переключение на источник данных PostgreSQL
     */
    @FXML
    private void handleSwitchToPostgres() {
        switchDataSource(GameDaoFactory.DataSourceType.POSTGRES);
    }

    /**
     * Выполняет переключение источника данных
     *
     * @param type тип источника данных для переключения
     */
    private void switchDataSource(GameDaoFactory.DataSourceType type) {
        try {
            appService.switchDataSource(type);
            refreshTable();
            updateDataSourceMenuLabel(type);
            updateStatus("Switched to: " + type);
        } catch (Exception e) {
            error("Error switching data source", e);
            showErrorAlert("Switch error", "Failed to switch to " + type);
        }
    }

    /**
     * Обновляет текстовую метку меню выбора источника данных
     *
     * @param type текущий тип источника данных
     */
    private void updateDataSourceMenuLabel(GameDaoFactory.DataSourceType type) {
        dataSourceMenu.setText(appService.getDataSourceLabel(type));
    }

    // endregion

    // region Game Management

    /**
     * Обрабатывает добавление новой игры
     */
    @FXML
    private void handleAddGame() {
        Optional<Game> newGame = appService.showAddGameDialog();
        newGame.ifPresent(this::processGameAddition);
    }

    /**
     * Обрабатывает процесс добавления новой игры с валидацией
     *
     * @param game новая игра для добавления
     */
    private void processGameAddition(Game game) {
        if (appService.validateGame(game)) {
            appService.addGame(game);
            refreshTable();
            updateStatus("Game '" + game.getTitle() + "' successfully added");
        } else {
            appService.showValidationError();
        }
    }

    /**
     * Обрабатывает редактирование выбранной игры
     */
    private void handleEditGame() {
        if (!tableManager.hasSelection()) {
            showErrorAlert("Error", "Select a game to edit!");
            return;
        }

        Game selectedGame = tableManager.getSelectedGame();
        Optional<Game> updatedGame = appService.showEditGameDialog(selectedGame);

        updatedGame.ifPresent(game -> {
            appService.updateGame(game);
            refreshTable();
            tableManager.scrollToGameById(game.getId());
            updateStatus("Game '" + game.getTitle() + "' updated");
        });
    }

    /**
     * Обрабатывает удаление выбранной игры
     */
    @FXML
    private void handleDeleteGame() {
        if (!tableManager.hasSelection()) {
            showErrorAlert("No game selected", "Please select a game to delete.");
            return;
        }

        Game selectedGame = tableManager.getSelectedGame();
        if (appService.confirmGameDeletion(selectedGame)) {
            appService.deleteGame(selectedGame);
            refreshTable();
            appService.showSuccessAlert("Success", "Game '" + selectedGame.getTitle() + "' successfully deleted.");
            updateStatus("Game '" + selectedGame.getTitle() + "' deleted");
        }
    }

    // endregion

    // region Session Planning

    /**
     * Обрабатывает планирование игровой сессии
     */
    @FXML
    private void handlePlanSession() {
        if (!tableManager.hasSelection()) {
            showErrorAlert("Error", "Select a game to plan session!");
            return;
        }

        Optional<LocalDate> selectedDate = appService.showSessionDatePicker();
        selectedDate.ifPresent(date -> {
            if (appService.validateSessionDate(date)) {
                Game selectedGame = tableManager.getSelectedGame();
                appService.planGameSession(selectedGame, date);
                refreshTable();
                tableManager.scrollToSelected();
                appService.showSuccessAlert("Session planned",
                        "Game: " + selectedGame.getTitle() + "\nDate: " + date);
                updateStatus("Session planned for '" + selectedGame.getTitle() + "'");
            } else {
                showErrorAlert("Error", "Session date cannot be in the past!");
            }
        });
    }

    // endregion

    // region Random Game & Filtering

    /**
     * Обрабатывает выбор случайной игры
     */
    @FXML
    private void handleRandomGame() {
        Game randomGame = appService.getRandomGame();
        if (randomGame != null) {
            appService.showRandomGameAlert(randomGame);
            updateStatus("Recommended game: '" + randomGame.getTitle() + "'");
            tableManager.selectGameById(randomGame.getId());
        } else {
            showErrorAlert("No games", "No games available for recommendation.");
        }
    }

    /**
     * Применяет выбранный фильтр к таблице игр
     */
    private void applyFilter() {
        try {
            String filter = filterComboBox.getValue();
            filterManager.applyFilter(filter, gameTable);
            updateItemsCount();
            updateStatus("Filter applied: " + filter);
        } catch (Exception e) {
            error("Error applying filter", e);
            showErrorAlert("Filter error", "Failed to apply filter");
        }
    }

    // endregion

    // region Utility Methods

    /**
     * Обрабатывает просмотр деталей выбранной игры
     */
    private void handleViewDetails() {
        if (tableManager.hasSelection()) {
            Game selectedGame = tableManager.getSelectedGame();
            gameContextMenu.showGameDetails(selectedGame);
            debug("Viewing game details: " + selectedGame.getTitle());
        }
    }

    /**
     * Обновляет данные в таблице игр
     */
    private void refreshTable() {
        try {
            List<Game> games = appService.getAllGames();
            tableManager.refreshTable(games);
            updateItemsCount();
            updateStatus("Table updated. Games: " + games.size());
        } catch (Exception e) {
            updateStatus("Error loading data");
            error("Error updating table", e);
        }
    }

    /**
     * Обновляет счетчик игр в интерфейсе
     */
    private void updateItemsCount() {
        try {
            int count = tableManager.getItemCount();
            if (itemsCountLabel != null) {
                itemsCountLabel.setText("Games in collection: " + count);

                // Обновляем подсказку с детальной информацией
                long activeCount = gameTable.getItems().stream()
                        .filter(g -> "Активно".equals(g.getStatus()))
                        .count();
                long inactiveCount = count - activeCount;

                TooltipManager.updateCountTooltip(itemsCountLabel, count, activeCount, inactiveCount);
            }
        } catch (Exception e) {
            debug("Failed to update game counter: " + e.getMessage());
        }
    }

    /**
     * Обновляет текстовую метку статуса в интерфейсе
     *
     * @param message сообщение для отображения
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Отображает уведомление об ошибке
     *
     * @param title заголовок ошибки
     * @param message текст сообщения об ошибке
     */
    private void showErrorAlert(String title, String message) {
        appService.showErrorAlert(title, message);
    }
    // endregion
}