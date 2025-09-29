package taskDao.module.util;

import taskDao.module.dao.Game;
import taskDao.module.service.ValidationService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Базовый абстрактный класс для диалогов работы с играми
 *
 * <p>Содержит общую логику и UI-компоненты для диалогов добавления
 * и редактирования игр, устраняя дублирование кода.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public abstract class BaseGameDialog {
    protected final Dialog<Game> dialog;
    protected final TextField titleField = new TextField();
    protected final ComboBox<String> genreComboBox = new ComboBox<>();
    protected final Spinner<Integer> minSpinner = new Spinner<>(1, 16, 1);
    protected final Spinner<Integer> maxSpinner = new Spinner<>(1, 16, 1);

    /**
     * Конструктор базового диалога
     *
     * @param title заголовок диалогового окна
     */
    protected BaseGameDialog(String title) {
        this.dialog = new Dialog<>();
        this.dialog.setTitle(title);
        initCommonUI();
    }

    /**
     * Инициализирует общие UI-компоненты диалога
     */
    protected void initCommonUI() {
        // Заполняем выпадающий список жанрами из валидации
        genreComboBox.getItems().addAll(ValidationService.VALID_GENRES);
        genreComboBox.setPromptText("Выберите жанр");

        GridPane grid = createCommonGrid();
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setupValidation();
        setupSpinners();
    }

    /**
     * Создает общую сетку размещения компонентов
     *
     * @return сконфигурированная сетка компонентов
     */
    protected GridPane createCommonGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);

        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Жанр:"), 0, 1);
        grid.add(genreComboBox, 1, 1);
        grid.add(new Label("Мин. игроков:"), 0, 2);
        grid.add(minSpinner, 1, 2);
        grid.add(new Label("Макс. игроков:"), 0, 3);
        grid.add(maxSpinner, 1, 3);

        return grid;
    }

    /**
     * Настраивает валидацию полей ввода
     */
    protected void setupValidation() {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Валидация полей в реальном времени
        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateFields(okButton));
        genreComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateFields(okButton));
    }

    /**
     * Настраивает спиннеры для выбора количества игроков
     */
    protected void setupSpinners() {
        minSpinner.getValueFactory().setValue(1);
        maxSpinner.getValueFactory().setValue(1);
    }

    /**
     * Валидирует поля ввода и управляет состоянием кнопки OK
     *
     * @param okButton кнопка OK для управления состоянием
     */
    protected void validateFields(Button okButton) {
        boolean isValid = !titleField.getText().trim().isEmpty() &&
                genreComboBox.getValue() != null &&
                !genreComboBox.getValue().isEmpty();
        okButton.setDisable(!isValid);
    }

    /**
     * Создает объект игры на основе данных из полей ввода
     *
     * @param id идентификатор игры
     * @return созданный объект игры
     */
    protected Game createGameFromFields(int id) {
        return new Game(
                id,
                titleField.getText().trim(),
                genreComboBox.getValue(),
                minSpinner.getValue(),
                maxSpinner.getValue()
        );
    }
}