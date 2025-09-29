package taskDao.module.managers;

import taskDao.module.dao.GameDao;
import taskDao.module.dao.GameDaoFactory;
import taskDao.module.service.GameService;
import taskDao.module.service.ValidationService;

/**
 * Менеджер для управления источниками данных приложения
 *
 * <p>Обеспечивает переключение между различными источниками хранения данных
 * (JSON, PostgreSQL) и предоставляет единый интерфейс для работы с данными.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class DataSourceManager {
    private GameService gameService;
    private final ValidationService validationService;

    /**
     * Конструктор инициализирует сервис валидации
     */
    public DataSourceManager() {
        this.validationService = new ValidationService();
    }

    /**
     * Переключает источник данных на указанный тип
     *
     * @param type тип источника данных для переключения
     * @return экземпляр GameService для работы с выбранным источником
     * @throws RuntimeException если переключение не удалось
     */
    public GameService switchDataSource(GameDaoFactory.DataSourceType type) {
        try {
            info("Switching to data source: " + type);
            GameDao dao = GameDaoFactory.createDao(type);
            this.gameService = new GameService(dao);
            info("Successfully switched to: " + type);
            return gameService;
        } catch (Exception e) {
            error("Error switching data source", e);
            throw new RuntimeException("Failed to switch to " + type, e);
        }
    }

    /**
     * Возвращает текущий сервис работы с играми
     *
     * @return экземпляр GameService
     */
    public GameService getGameService() {
        return gameService;
    }

    /**
     * Возвращает сервис валидации данных
     *
     * @return экземпляр ValidationService
     */
    public ValidationService getValidationService() {
        return validationService;
    }
}