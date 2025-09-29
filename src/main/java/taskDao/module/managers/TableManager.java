package taskDao.module.managers;

import taskDao.module.dao.Game;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import java.util.List;

import static taskDao.module.util.LoggerUtil.debug;

/**
 * Менеджер для управления таблицей игр
 *
 * <p>Обеспечивает централизованное управление состоянием таблицы,
 * включая обновление данных, сохранение выделения и навигацию.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class TableManager {
    private final TableView<Game> gameTable;
    private Game lastSelectedGame;
    private int lastSelectedIndex = -1;

    /**
     * Конструктор инициализирует менеджер с указанной таблицей
     *
     * @param gameTable таблица игр для управления
     */
    public TableManager(TableView<Game> gameTable) {
        this.gameTable = gameTable;
    }

    /**
     * Обновляет таблицу с сохранением текущего выделения
     *
     * @param games новый список игр для отображения
     */
    public void refreshTable(List<Game> games) {
        saveSelectionState();
        gameTable.setItems(FXCollections.observableArrayList(games));
        restoreSelectionState();
        debug("Table refreshed, records: " + games.size());
    }

    /**
     * Обновляет таблицу без сохранения выделения (простая замена данных)
     *
     * @param games новый список игр для отображения
     */
    public void setTableItems(List<Game> games) {
        gameTable.setItems(FXCollections.observableArrayList(games));
        debug("Table data set, records: " + games.size());
    }

    /**
     * Сохраняет текущее состояние выделения в таблице
     */
    public void saveSelectionState() {
        Game selected = gameTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.lastSelectedGame = selected;
            this.lastSelectedIndex = gameTable.getSelectionModel().getSelectedIndex();
            debug("Selection saved: " + selected.getTitle() + " (index: " + lastSelectedIndex + ")");
        }
    }

    /**
     * Восстанавливает выделение после обновления данных таблицы
     */
    public void restoreSelectionState() {
        if (lastSelectedGame != null) {
            // Пытаемся найти ту же игру по ID
            for (int i = 0; i < gameTable.getItems().size(); i++) {
                Game game = gameTable.getItems().get(i);
                if (game.getId() == lastSelectedGame.getId()) {
                    gameTable.getSelectionModel().select(i);
                    debug("Selection restored for game: " + game.getTitle());
                    clearSavedState();
                    return;
                }
            }

            // Если игра не найдена, пытаемся восстановить по индексу
            if (lastSelectedIndex >= 0 && lastSelectedIndex < gameTable.getItems().size()) {
                gameTable.getSelectionModel().select(lastSelectedIndex);
                debug("Selection restored by index: " + lastSelectedIndex);
            }
        }

        clearSavedState();
    }

    /**
     * Очищает сохраненное состояние выделения
     */
    private void clearSavedState() {
        lastSelectedGame = null;
        lastSelectedIndex = -1;
    }

    /**
     * Очищает текущее выделение в таблице
     */
    public void clearSelection() {
        gameTable.getSelectionModel().clearSelection();
        clearSavedState();
        debug("Selection cleared");
    }

    /**
     * Возвращает выбранную в таблице игру
     *
     * @return выбранная игра или null если ничего не выбрано
     */
    public Game getSelectedGame() {
        return gameTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Проверяет, есть ли выбранная игра в таблице
     *
     * @return true если есть выбранная игра, false в противном случае
     */
    public boolean hasSelection() {
        return getSelectedGame() != null;
    }

    /**
     * Прокручивает таблицу к выбранной игре
     */
    public void scrollToSelected() {
        if (hasSelection()) {
            int index = gameTable.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                gameTable.scrollTo(index);
                debug("Scrolled to index: " + index);
            }
        }
    }

    /**
     * Прокручивает таблицу к указанному индексу и выделяет его
     *
     * @param index индекс для прокрутки
     */
    public void scrollToIndex(int index) {
        if (index >= 0 && index < gameTable.getItems().size()) {
            gameTable.scrollTo(index);
            gameTable.getSelectionModel().select(index);
            debug("Scrolled to index: " + index);
        }
    }

    /**
     * Прокручивает таблицу к игре с указанным ID и выделяет ее
     *
     * @param gameId ID игры для поиска
     */
    public void scrollToGameById(int gameId) {
        for (int i = 0; i < gameTable.getItems().size(); i++) {
            if (gameTable.getItems().get(i).getId() == gameId) {
                scrollToIndex(i);
                return;
            }
        }
        debug("Game with ID " + gameId + " not found for scrolling");
    }

    /**
     * Возвращает количество элементов в таблице
     *
     * @return количество игр в таблице
     */
    public int getItemCount() {
        return gameTable.getItems().size();
    }

    /**
     * Проверяет, пуста ли таблица
     *
     * @return true если таблица пуста, false в противном случае
     */
    public boolean isEmpty() {
        return gameTable.getItems().isEmpty();
    }

    /**
     * Обновляет конкретную строку в таблице
     *
     * @param game игра с обновленными данными
     */
    public void refreshGameRow(Game game) {
        int index = findGameIndexById(game.getId());
        if (index != -1) {
            gameTable.getItems().set(index, game);
            gameTable.refresh();
            debug("Row updated for game: " + game.getTitle());
        }
    }

    /**
     * Находит индекс игры по ID в текущих данных таблицы
     *
     * @param gameId ID игры для поиска
     * @return индекс игры или -1 если не найдена
     */
    private int findGameIndexById(int gameId) {
        for (int i = 0; i < gameTable.getItems().size(); i++) {
            if (gameTable.getItems().get(i).getId() == gameId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Выделяет игру по ID в таблице
     *
     * @param gameId ID игры для выделения
     * @return true если игра найдена и выделена, false в противном случае
     */
    public boolean selectGameById(int gameId) {
        int index = findGameIndexById(gameId);
        if (index != -1) {
            gameTable.getSelectionModel().select(index);
            scrollToIndex(index);
            debug("Selected game with ID: " + gameId);
            return true;
        }
        debug("Game with ID " + gameId + " not found for selection");
        return false;
    }

    /**
     * Возвращает текущий выбранный индекс в таблице
     *
     * @return индекс выбранной строки или -1 если ничего не выбрано
     */
    public int getSelectedIndex() {
        return gameTable.getSelectionModel().getSelectedIndex();
    }

    /**
     * Устанавливает выделение по указанному индексу
     *
     * @param index индекс для выделения
     */
    public void selectIndex(int index) {
        if (index >= 0 && index < gameTable.getItems().size()) {
            gameTable.getSelectionModel().select(index);
        }
    }
}