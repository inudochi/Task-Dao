module coursework.listofgames {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires telegrambots.meta;
    requires telegrambots;

    opens taskDao.main to javafx.fxml;
    exports taskDao.main;
    exports taskDao.module.dao;
    exports taskDao.module.service;
    exports taskDao.module.managers;
    exports taskDao.module.util;
}