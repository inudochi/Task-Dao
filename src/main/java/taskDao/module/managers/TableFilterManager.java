package taskDao.module.managers;

import taskDao.module.dao.Game;
import taskDao.module.service.GameService;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Менеджер для управления фильтрацией данных в таблице игр
 *
 * <p>Обеспечивает применение различных фильтров к отображаемому списку игр
 * и управление элементами интерфейса для фильтрации.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class TableFilterManager {
    private final GameService gameService;

    /**
     * Конструктор инициализирует сервис работы с играми
     *
     * @param gameService сервис для получения данных об играх
     */
    public TableFilterManager(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Применяет указанный фильтр к таблице игр
     *
     * @param filter тип фильтра для применения
     * @param gameTable таблица игр для обновления
     * @throws RuntimeException если применение фильтра не удалось
     */
    public void applyFilter(String filter, TableView<Game> gameTable) {
        try {
            List<Game> filteredGames = switch (filter) {
                case "Одиночные (1 игрок)" -> gameService.getSinglePlayerGames();
                case "Для компании (2+ игроков)" -> gameService.getMultiplayerGames();
                default -> gameService.getAllGames();
            };
            gameTable.setItems(FXCollections.observableArrayList(filteredGames));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка применения фильтра: " + filter, e);
        }
    }

    /**
     * Настраивает выпадающий список фильтров
     *
     * @param comboBox комбобокс для настройки
     */
    public void setupFilterComboBox(javafx.scene.control.ComboBox<String> comboBox) {
        comboBox.getItems().addAll("Все игры", "Одиночные (1 игрок)", "Для компании (2+ игроков)");
        comboBox.setValue("Все игры");
    }
}