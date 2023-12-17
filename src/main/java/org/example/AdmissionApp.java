package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdmissionApp extends JFrame implements ActionListener {
    private JTextField lastNameField, firstNameField, facultyField;
    private JButton searchButton, addButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public AdmissionApp() {
        setTitle("Приемная комиссия");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1920);

        // Создание поля ввода для фамилии абитуриента
        lastNameField = new JTextField(20);

        // Создание поля ввода для имени абитуриента
        firstNameField = new JTextField(20);

        // Создание поля ввода для названия факультета
        facultyField = new JTextField(20);

        // Создание кнопки поиска
        searchButton = new JButton("Поиск");

        // Создание кнопки добавления
        addButton = new JButton("Добавить");



        // Добавление слушателей событий для кнопок
        searchButton.addActionListener(this);
        addButton.addActionListener(this);

        // Создание таблицы
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Фамилия");
        tableModel.addColumn("Имя");
        tableModel.addColumn("Факультет");
        table = new JTable(tableModel);

        // Создание панели для полей ввода и кнопок
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Фамилия абитуриента: "));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Имя абитуриента: "));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Название факультета: "));
        inputPanel.add(facultyField);
        inputPanel.add(searchButton);
        inputPanel.add(addButton);

        // Создание панели с таблицей
        JScrollPane scrollPane = new JScrollPane(table);

        // Добавление компонентов на форму
        Container container = getContentPane();
        container.add(inputPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        // Подключение к базе данных
        connectToDatabase();
        searchApplicants();
    }

    // Подключение к базе данных
    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/admission";
        String username = "root";
        String password = "145263";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Поиск абитуриентов по фамилии и факультету
    private void searchApplicants() {
        String lastName = lastNameField.getText();
        String faculty = facultyField.getText();

        // Очистка таблицы перед выполнением нового запроса
        tableModel.setRowCount(0);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM applicants WHERE last_name LIKE ? AND faculty LIKE ?");
            statement.setString(1, "%" + lastName + "%");
            statement.setString(2, "%" + faculty + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String applicantLastName = resultSet.getString("last_name");
                String firstName = resultSet.getString("first_name");
                String applicantFaculty = resultSet.getString("faculty");

                tableModel.addRow(new Object[]{applicantLastName, firstName, applicantFaculty});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        // Добавление нового абитуриента в базу данных
    private void addApplicant() {
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        String faculty = facultyField.getText();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO applicants (last_name, first_name, faculty) VALUES (?, ?, ?)");
            statement.setString(1, lastName);
            statement.setString(2, firstName);
            statement.setString(3, faculty);
            statement.executeUpdate();

            // Очистка полей ввода после добавления абитуриента
            lastNameField.setText("");
            firstNameField.setText("");
            facultyField.setText("");

            // Обновление таблицы
            searchApplicants();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Обработчик событий кнопок
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            searchApplicants();
        } else if (e.getSource() == addButton) {
            addApplicant();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdmissionApp app = new AdmissionApp();
            app.setVisible(true);
        });
    }
}