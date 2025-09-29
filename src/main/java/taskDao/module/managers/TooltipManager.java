package taskDao.module.managers;

import javafx.scene.control.*;
import javafx.util.Duration;

import static taskDao.module.util.LoggerUtil.debug;
import static taskDao.module.util.LoggerUtil.error;

/**
 * Менеджер для управления всплывающими подсказками интерфейса
 *
 * <p>Обеспечивает централизованную настройку и конфигурацию подсказок
 * для всех элементов пользовательского интерфейса приложения.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class TooltipManager {

    /**
     * Настраивает все подсказки для главного окна приложения
     *
     * @param gameTable таблица игр
     * @param dataSourceMenu меню выбора источника данных
     * @param filterComboBox комбобокс фильтрации
     * @param addGameButton кнопка добавления игры
     * @param deleteGameButton кнопка удаления игры
     * @param randomGameButton кнопка случайной игры
     * @param statusLabel метка статуса
     * @param itemsCountLabel метка счетчика игр
     */
    public static void setupGameControllerTooltips(TableView<?> gameTable,
                                                   MenuButton dataSourceMenu,
                                                   ComboBox<?> filterComboBox,
                                                   Button addGameButton,
                                                   Button deleteGameButton,
                                                   Button randomGameButton,
                                                   Label statusLabel,
                                                   Label itemsCountLabel) {
        try {
            setupTableTooltips(gameTable);
            setupDataSourceTooltips(dataSourceMenu);
            setupFilterTooltips(filterComboBox);
            setupButtonTooltips(addGameButton, deleteGameButton, randomGameButton);
            setupLabelTooltips(statusLabel, itemsCountLabel);

            debug("All tooltips successfully initialized");
        } catch (Exception e) {
            error("Error setting up tooltips", e);
        }
    }

    /**
     * Настраивает подсказки для таблицы игр
     *
     * @param gameTable таблица игр для настройки
     */
    private static void setupTableTooltips(TableView<?> gameTable) {
        if (gameTable != null) {
            Tooltip tableTooltip = new Tooltip(
                    "Управление таблицей:\n" +
                            "• Двойной клик - быстрая запись игровой сессии\n" +
                            "• Правый клик - дополнительные действия\n" +
                            "• Выделение строки - выбор игры для операций"
            );
            configureTooltip(tableTooltip);
            gameTable.setTooltip(tableTooltip);
        }
    }

    /**
     * Настраивает подсказки для выбора источника данных
     *
     * @param dataSourceMenu меню выбора источника данных
     */
    private static void setupDataSourceTooltips(MenuButton dataSourceMenu) {
        if (dataSourceMenu != null) {
            Tooltip sourceTooltip = new Tooltip(
                    "Выбор источника хранения данных:\n" +
                            "• JSON - данные хранятся в локальном файле\n" +
                            "• PostgreSQL - данные в базе данных"
            );
            configureTooltip(sourceTooltip);
            dataSourceMenu.setTooltip(sourceTooltip);
        }
    }

    /**
     * Настраивает подсказки для комбобокса фильтрации
     *
     * @param filterComboBox комбобокс фильтрации игр
     */
    private static void setupFilterTooltips(ComboBox<?> filterComboBox) {
        if (filterComboBox != null) {
            Tooltip filterTooltip = new Tooltip(
                    "Фильтрация игр по количеству игроков:\n" +
                            "• Все игры - показать всю коллекцию\n" +
                            "• Одиночные - игры для одного игрока\n" +
                            "• Для компании - игры для 2+ игроков"
            );
            configureTooltip(filterTooltip);
            filterComboBox.setTooltip(filterTooltip);
        }
    }

    /**
     * Настраивает подсказки для кнопок управления
     *
     * @param addGameButton кнопка добавления игры
     * @param deleteGameButton кнопка удаления игры
     * @param randomGameButton кнопка случайной игры
     */
    private static void setupButtonTooltips(Button addGameButton,
                                            Button deleteGameButton,
                                            Button randomGameButton) {
        // Подсказка для кнопки добавления игры
        if (addGameButton != null) {
            Tooltip addTooltip = new Tooltip(
                    "Добавление новой игры:\n" +
                            "• Открывает диалог ввода данных\n" +
                            "• Поддерживаются все основные жанры\n" +
                            "• Автоматическая валидация введенных данных"
            );
            configureTooltip(addTooltip);
            addGameButton.setTooltip(addTooltip);
        }

        // Подсказка для кнопки удаления игры
        if (deleteGameButton != null) {
            Tooltip deleteTooltip = new Tooltip(
                    "Удаление выбранной игры:\n" +
                            "• Требуется выделение игры в таблице\n" +
                            "• Запрашивает подтверждение операции\n" +
                            "• Данные удаляются безвозвратно"
            );
            configureTooltip(deleteTooltip);
            deleteGameButton.setTooltip(deleteTooltip);
        }

        // Подсказка для кнопки случайной игры
        if (randomGameButton != null) {
            Tooltip randomTooltip = new Tooltip(
                    "Случайный выбор игры:\n" +
                            "• Предпочтение отдается неигранным играм\n" +
                            "• Учитывает статус активности\n" +
                            "• Полезно для выбора игры для вечера"
            );
            configureTooltip(randomTooltip);
            randomGameButton.setTooltip(randomTooltip);
        }
    }

    /**
     * Настраивает подсказки для информационных меток
     *
     * @param statusLabel метка статуса операций
     * @param itemsCountLabel метка счетчика игр
     */
    private static void setupLabelTooltips(Label statusLabel, Label itemsCountLabel) {
        // Подсказка для статусной метки
        if (statusLabel != null) {
            Tooltip statusTooltip = new Tooltip(
                    "Статус операций:\n" +
                            "• Отображает текущее состояние приложения\n" +
                            "• Показывает сообщения об ошибках\n" +
                            "• Информирует о выполненных действиях"
            );
            configureTooltip(statusTooltip);
            statusLabel.setTooltip(statusTooltip);
        }

        // Подсказка для счетчика игр
        if (itemsCountLabel != null) {
            Tooltip countTooltip = new Tooltip(
                    "Статистика коллекции:\n" +
                            "• Отображает общее количество игр\n" +
                            "• Обновляется при изменении данных"
            );
            configureTooltip(countTooltip);
            itemsCountLabel.setTooltip(countTooltip);
        }
    }

    /**
     * Общая конфигурация для всех подсказок
     *
     * @param tooltip подсказка для конфигурации
     */
    private static void configureTooltip(Tooltip tooltip) {
        tooltip.setShowDelay(Duration.millis(400));
        tooltip.setHideDelay(Duration.millis(200));
        tooltip.setStyle("-fx-font-size: 12px; " +
                "-fx-text-fill: #2c3e50; " +
                "-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #dee2e6; " +
                "-fx-border-width: 1px;");
    }

    /**
     * Создает подсказку для диалоговых окон
     *
     * @param text текст подсказки
     * @return сконфигурированная подсказка
     */
    public static Tooltip createDialogTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        configureTooltip(tooltip);
        return tooltip;
    }

    /**
     * Обновляет подсказку для счетчика игр с детальной статистикой
     *
     * @param itemsCountLabel метка счетчика игр
     * @param total общее количество игр
     * @param active количество активных игр
     * @param inactive количество неактивных игр
     */
    public static void updateCountTooltip(Label itemsCountLabel, long total, long active, long inactive) {
        if (itemsCountLabel != null) {
            Tooltip countTooltip = new Tooltip(
                    String.format("Статистика коллекции:\n• Всего игр: %d\n• Активных: %d\n• Неактивных: %d",
                            total, active, inactive)
            );
            configureTooltip(countTooltip);
            itemsCountLabel.setTooltip(countTooltip);
        }
    }
}