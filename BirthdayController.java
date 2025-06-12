import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import java.sql.*;
import java.time.LocalDate;

public class BirthdayController {
    @FXML private TextField nameField, searchField;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Birthday> birthdayTable;

    private ObservableList<Birthday> birthdays = FXCollections.observableArrayList();

    public void initialize() {
        loadBirthdays();
        checkTodaysBirthdays();
    }

    public void loadBirthdays() {
        birthdays.clear();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM birthdays ORDER BY MONTH(birthdate), DAY(birthdate)")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                birthdays.add(new Birthday(rs.getInt("id"), rs.getString("name"), rs.getDate("birthdate").toLocalDate()));
            }
            birthdayTable.setItems(birthdays);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBirthday() {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO birthdays (name, birthdate) VALUES (?, ?)")) {

            stmt.setString(1, nameField.getText());
            stmt.setDate(2, Date.valueOf(datePicker.getValue()));
            stmt.executeUpdate();
            loadBirthdays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchBirthdays() {
        String keyword = searchField.getText().toLowerCase();
        ObservableList<Birthday> filtered = birthdays.filtered(b ->
            b.getName().toLowerCase().contains(keyword) ||
            b.getBirthdate().getMonth().name().toLowerCase().contains(keyword));
        birthdayTable.setItems(filtered);
    }

    public void checkTodaysBirthdays() {
        LocalDate today = LocalDate.now();
        for (Birthday b : birthdays) {
            if (b.getBirthdate().getMonth() == today.getMonth() &&
                b.getBirthdate().getDayOfMonth() == today.getDayOfMonth()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("আজকের জন্মদিন");
                alert.setHeaderText(null);
                alert.setContentText("আজ " + b.getName() + " এর জন্মদিন!");
                alert.show();
            }
        }
    }
}
