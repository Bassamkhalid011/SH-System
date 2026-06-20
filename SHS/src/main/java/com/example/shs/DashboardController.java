package com.example.shs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.HashSet;

public class DashboardController implements Initializable {
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button profileButton;
    @FXML private Button marksButton;
    @FXML private Button findUniversitiesButton;
    @FXML private Button viewAllButton;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox mainContent;
    @FXML private Label userNameLabel;
    @FXML private Label appliedNumberCounter;
    @FXML private Label savedNumberCounter;

    // Quick access buttons
    @FXML private Button profileQuickButton;
    @FXML private Button marksQuickButton;
    @FXML private Button universitiesQuickButton;
    @FXML private Button savedButton;
    @FXML private Button appliedButton;

    private DataManager dataManager = DataManager.getInstance();

    // Initialize the sets for saved and applied universities
    private Set<University> savedUniversities = new HashSet<>();
    private Set<University> appliedUniversities = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student currentStudent = dataManager.getCurrentStudent();
        if (currentStudent != null) {
            welcomeLabel.setText("Welcome, " + currentStudent.getFullName());
            // Update the user name label in sidebar if it exists
            if (userNameLabel != null) {
                userNameLabel.setText(currentStudent.getFullName());
            }
        }

        // Load saved/applied from file
        savedUniversities   = dataManager.getSavedUniversities(currentStudent);
        appliedUniversities = dataManager.getAppliedUniversities(currentStudent);

        // Initialize quick buttons - this is crucial for functionality
        initializeQuickButtons();

        // Update the dashboard statistics
        updateDashboardStatsRobust();

        // Setup counter buttons
        setupCounterButtons();
    }

    private void setupCounterButtons() {
        if (savedButton != null) {
            savedButton.setOnAction(e -> showSavedUniversities());
        }
        if (appliedButton != null) {
            appliedButton.setOnAction(e -> showAppliedUniversities());
        }
    }

    @FXML
    private void handleSavedUniversities() {
        showSavedUniversities();
    }

    @FXML
    private void handleAppliedUniversities() {
        showAppliedUniversities();
    }

    private void showSavedUniversities() {
        mainContent.getChildren().clear();

        Button backButton = createStyledButton("← Back to Dashboard", "#6c757d");
        backButton.setOnAction(e -> returnToDashboard());

        Label title = new Label("Saved Universities (" + savedUniversities.size() + ")");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.DARKGREEN);

        VBox universitiesList = new VBox(15);

        if (savedUniversities.isEmpty()) {
            Label noResultsLabel = new Label("You haven't saved any universities yet!");
            noResultsLabel.setTextFill(Color.GRAY);
            noResultsLabel.setFont(Font.font("Arial", 16));
            universitiesList.getChildren().add(noResultsLabel);
        } else {
            for (University uni : savedUniversities) {
                VBox uniCard = createUniversityCardForSavedApplied(uni, true, false);
                universitiesList.getChildren().add(uniCard);
            }
        }

        mainContent.getChildren().addAll(backButton, title, universitiesList);
    }

    private void showAppliedUniversities() {
        mainContent.getChildren().clear();

        Button backButton = createStyledButton("← Back to Dashboard", "#6c757d");
        backButton.setOnAction(e -> returnToDashboard());

        Label title = new Label("Applied Universities (" + appliedUniversities.size() + ")");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.DARKBLUE);

        VBox universitiesList = new VBox(15);

        if (appliedUniversities.isEmpty()) {
            Label noResultsLabel = new Label("You haven't applied to any universities yet!");
            noResultsLabel.setTextFill(Color.GRAY);
            noResultsLabel.setFont(Font.font("Arial", 16));
            universitiesList.getChildren().add(noResultsLabel);
        } else {
            for (University uni : appliedUniversities) {
                VBox uniCard = createUniversityCardForSavedApplied(uni, false, true);
                universitiesList.getChildren().add(uniCard);
            }
        }

        mainContent.getChildren().addAll(backButton, title, universitiesList);
    }

    private VBox createUniversityCardForSavedApplied(University uni, boolean isSaved, boolean isApplied) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));

        String cardColor = isSaved ? "#e8f5e8" : "#e8f0ff";
        card.setStyle("-fx-background-color: " + cardColor + "; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label nameLabel = new Label(uni.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.DARKBLUE);

        Label statusLabel = new Label(isSaved ? "💾 SAVED" : "📝 APPLIED");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setTextFill(isSaved ? Color.DARKGREEN : Color.DARKBLUE);

        Label locationLabel = new Label("📍 " + uni.getLocation() + " (" + uni.getType() + ")");
        locationLabel.setFont(Font.font("Arial", 14));

        Label programsLabel = new Label("📚 Programs: " + uni.getPrograms());
        programsLabel.setFont(Font.font("Arial", 14));

        Label requirementsLabel = new Label(String.format("📋 Requirements: Matric: %.0f%%, FSC: %.0f%%, NTS: %.0f%%",
                uni.getMatricRequired(), uni.getFscRequired(), uni.getNtsRequired()));
        requirementsLabel.setFont(Font.font("Arial", 14));

        Button removeButton = new Button(isSaved ? "Remove from Saved" : "Remove from Applied");
        removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 4;");
        removeButton.setOnAction(e -> {
            if (isSaved) {
                savedUniversities.remove(uni);
                if (savedNumberCounter != null) {
                    savedNumberCounter.setText(String.valueOf(savedUniversities.size()));
                }
                showSavedUniversities();
            } else {
                appliedUniversities.remove(uni);
                if (appliedNumberCounter != null) {
                    appliedNumberCounter.setText(String.valueOf(appliedUniversities.size()));
                }
                showAppliedUniversities();
            }
        });

        card.getChildren().addAll(nameLabel, statusLabel, locationLabel, programsLabel, requirementsLabel, removeButton);
        return card;
    }

    private void initializeQuickButtons() {
        System.out.println("Initializing quick buttons...");

        if (profileQuickButton != null) {
            System.out.println("Profile quick button found - setting up...");
            // Ensure button is visible and properly sized
            profileQuickButton.setVisible(true);
            profileQuickButton.setManaged(true);
            profileQuickButton.setMinSize(220, 150);
            profileQuickButton.setPrefSize(220, 150);
            profileQuickButton.setMaxSize(220, 150);

            // Set action handler
            profileQuickButton.setOnAction(e -> {
                System.out.println("Profile quick button clicked!");
                handleProfile();
            });

            // Add hover effects
            profileQuickButton.setOnMouseEntered(e -> {
                profileQuickButton.setStyle(profileQuickButton.getStyle() +
                        "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            });

            profileQuickButton.setOnMouseExited(e -> {
                profileQuickButton.setStyle(profileQuickButton.getStyle().replace(
                        "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
            });
        } else {
            System.out.println("Profile quick button is null!");
        }

        if (marksQuickButton != null) {
            System.out.println("Marks quick button found - setting up...");
            marksQuickButton.setVisible(true);
            marksQuickButton.setManaged(true);
            marksQuickButton.setMinSize(220, 150);
            marksQuickButton.setPrefSize(220, 150);
            marksQuickButton.setMaxSize(220, 150);

            // Set action handler
            marksQuickButton.setOnAction(e -> {
                System.out.println("Marks quick button clicked!");
                handleMarks();
            });

            // Add hover effects
            marksQuickButton.setOnMouseEntered(e -> {
                marksQuickButton.setStyle(marksQuickButton.getStyle() +
                        "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            });

            marksQuickButton.setOnMouseExited(e -> {
                marksQuickButton.setStyle(marksQuickButton.getStyle().replace(
                        "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
            });
        } else {
            System.out.println("Marks quick button is null!");
        }

        if (universitiesQuickButton != null) {
            System.out.println("Universities quick button found - setting up...");
            universitiesQuickButton.setVisible(true);
            universitiesQuickButton.setManaged(true);
            universitiesQuickButton.setMinSize(220, 150);
            universitiesQuickButton.setPrefSize(220, 150);
            universitiesQuickButton.setMaxSize(220, 150);

            // Set action handler
            universitiesQuickButton.setOnAction(e -> {
                System.out.println("Universities quick button clicked!");
                handleFindUniversities();
            });

            // Add hover effects
            universitiesQuickButton.setOnMouseEntered(e -> {
                universitiesQuickButton.setStyle(universitiesQuickButton.getStyle() +
                        "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            });

            universitiesQuickButton.setOnMouseExited(e -> {
                universitiesQuickButton.setStyle(universitiesQuickButton.getStyle().replace(
                        "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
            });
        } else {
            System.out.println("Universities quick button is null!");
        }

        System.out.println("Quick buttons initialization completed.");
    }

    // More robust method to update dashboard statistics
    private void updateDashboardStatsRobust() {
        if (mainContent == null) {
            System.out.println("mainContent is null!");
            return;
        }

        // Get current statistics
        Map<String, Object> stats = dataManager.getStatistics();

        // Search for statistics labels and buttons more carefully
        updateStatisticsRecursively(mainContent, stats);
    }

    private void updateStatisticsRecursively(VBox container, Map<String, Object> stats) {
        for (int i = 0; i < container.getChildren().size(); i++) {
            if (container.getChildren().get(i) instanceof VBox) {
                VBox childVBox = (VBox) container.getChildren().get(i);

                // Check if this VBox contains statistics by looking for specific labels
                boolean isStatsBox = false;
                for (int j = 0; j < childVBox.getChildren().size(); j++) {
                    if (childVBox.getChildren().get(j) instanceof Label) {
                        Label label = (Label) childVBox.getChildren().get(j);
                        if (label.getText().contains("System Statistics") ||
                                label.getText().contains("Total Registered Students") ||
                                label.getText().contains("Total Universities") ||
                                label.getText().contains("Students with Complete Marks")) {
                            isStatsBox = true;
                            break;
                        }
                    }
                }

                if (isStatsBox) {
                    updateStatisticsBox(childVBox, stats);
                } else {
                    // Recursively search in nested VBoxes
                    updateStatisticsRecursively(childVBox, stats);
                }
            }
        }
    }

    private void updateStatisticsBox(VBox statsBox, Map<String, Object> stats) {
        // Update the labels in the statistics box
        for (int i = 0; i < statsBox.getChildren().size(); i++) {
            if (statsBox.getChildren().get(i) instanceof Label) {
                Label label = (Label) statsBox.getChildren().get(i);
                String text = label.getText();

                if (text.startsWith("Total Registered Students:")) {
                    label.setText("Total Registered Students: " + stats.get("totalStudents"));
                } else if (text.startsWith("Total Universities:")) {
                    label.setText("Total Universities: " + stats.get("totalUniversities"));
                } else if (text.startsWith("Students with Complete Marks:")) {
                    label.setText("Students with Complete Marks: " + stats.get("studentsWithCompleteMarks"));
                }
            } else if (statsBox.getChildren().get(i) instanceof HBox) {
                HBox hbox = (HBox) statsBox.getChildren().get(i);
                // Check if this HBox contains export buttons
                if (hbox.getChildren().size() >= 2) {
                    boolean hasExportButtons = false;
                    Button exportButton = null;
                    Button backupButton = null;

                    for (int j = 0; j < hbox.getChildren().size(); j++) {
                        if (hbox.getChildren().get(j) instanceof Button) {
                            Button btn = (Button) hbox.getChildren().get(j);
                            if (btn.getText().contains("Export")) {
                                exportButton = btn;
                                hasExportButtons = true;
                            } else if (btn.getText().contains("Backup")) {
                                backupButton = btn;
                            }
                        }
                    }

                    // Set up event handlers for the buttons if found
                    if (hasExportButtons && exportButton != null) {
                        exportButton.setOnAction(e -> showExportOptions());
                    }
                    if (backupButton != null) {
                        backupButton.setOnAction(e -> handleBackup());
                    }
                }
            }
        }
    }

    @FXML
    private void handleLogout() {
        // Save data before logout
        dataManager.saveData();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfile() {
        System.out.println("Navigating to Profile Settings...");
        showProfileSettings();
    }

    @FXML
    private void handleMarks() {
        System.out.println("Navigating to Marks Entry...");
        showMarksEntry();
    }

    @FXML
    private void handleFindUniversities() {
        System.out.println("Navigating to University Finder...");
        showUniversityFinder();
    }

    @FXML
    private void handleViewAll() {
        showAllUniversities();
    }

    // Add a method to return to dashboard
    private void returnToDashboard() {
        // Reload the FXML to restore original content
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 700);
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProfileSettings() {
        mainContent.getChildren().clear();

        Student currentStudent = dataManager.getCurrentStudent();

        // Add back button
        Button backButton = createStyledButton("← Back to Dashboard", "#6c757d");
        backButton.setOnAction(e -> returnToDashboard());

        Label title = new Label("Profile Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        VBox form = new VBox(15);
        form.setMaxWidth(400);

        TextField nameField = new TextField(currentStudent.getFullName());
        nameField.setPromptText("Full Name");
        nameField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        TextField emailField = new TextField(currentStudent.getEmail());
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password (leave blank to keep current)");
        passwordField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        Button updateButton = createStyledButton("Update Profile", "#007bff");

        updateButton.setOnAction(e -> {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newPassword = passwordField.getText();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                showAlert("Error", "Name and email cannot be empty!");
                return;
            }

            currentStudent.setFullName(newName);
            currentStudent.setEmail(newEmail);

            if (!newPassword.isEmpty()) {
                currentStudent.setPassword(newPassword);
            }

            // Save to file
            dataManager.updateStudentProfile(currentStudent);
            welcomeLabel.setText("Welcome, " + currentStudent.getFullName());
            showAlert("Success", "Profile updated successfully!");
        });

        form.getChildren().addAll(
                new Label("Full Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("New Password:"), passwordField,
                updateButton
        );

        mainContent.getChildren().addAll(backButton, title, form);
    }

    private void showMarksEntry() {
        mainContent.getChildren().clear();

        Student currentStudent = dataManager.getCurrentStudent();

        Button backButton = createStyledButton("← Back to Dashboard", "#6c757d");
        backButton.setOnAction(e -> returnToDashboard());

        Label title = new Label("Enter Your Academic Marks");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label subtitle = new Label("Enter obtained marks AND total marks for accurate aggregate calculation");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.GRAY);

        VBox form = new VBox(12);
        form.setMaxWidth(560);

        String fieldStyle = "-fx-padding: 10; -fx-font-size: 13; -fx-pref-width: 160;";
        String labelStyle = "-fx-font-size: 13; -fx-font-weight: bold;";

        // ── MATRIC ──
        Label matricHeader = new Label("Matric / SSC (Total: usually 1100)");
        matricHeader.setStyle(labelStyle);
        matricHeader.setTextFill(Color.DARKGREEN);
        TextField matricObtained = new TextField(currentStudent.getMatricMarks() > 0 ? String.valueOf((int)currentStudent.getMatricMarks()) : "");
        matricObtained.setPromptText("Obtained Marks (e.g. 950)");
        matricObtained.setStyle(fieldStyle);
        TextField matricTotal = new TextField(currentStudent.getMatricTotal() > 0 ? String.valueOf((int)currentStudent.getMatricTotal()) : "1100");
        matricTotal.setPromptText("Total Marks (e.g. 1100)");
        matricTotal.setStyle(fieldStyle);
        HBox matricRow = new HBox(10, matricObtained, new Label("out of"), matricTotal);
        matricRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // ── FSC ──
        Label fscHeader = new Label("FSC / HSSC (Total: usually 1100)");
        fscHeader.setStyle(labelStyle);
        fscHeader.setTextFill(Color.DARKBLUE);
        TextField fscObtained = new TextField(currentStudent.getFscMarks() > 0 ? String.valueOf((int)currentStudent.getFscMarks()) : "");
        fscObtained.setPromptText("Obtained Marks (e.g. 900)");
        fscObtained.setStyle(fieldStyle);
        TextField fscTotal = new TextField(currentStudent.getFscTotal() > 0 ? String.valueOf((int)currentStudent.getFscTotal()) : "1100");
        fscTotal.setPromptText("Total Marks (e.g. 1100)");
        fscTotal.setStyle(fieldStyle);
        HBox fscRow = new HBox(10, fscObtained, new Label("out of"), fscTotal);
        fscRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // ── NTS / Entry Test ──
        Label ntsHeader = new Label("NTS / Entry Test (for COMSATS, FAST, PU etc.)");
        ntsHeader.setStyle(labelStyle);
        ntsHeader.setTextFill(Color.DARKORANGE);
        TextField ntsObtained = new TextField(currentStudent.getNtsMarks() > 0 ? String.valueOf((int)currentStudent.getNtsMarks()) : "");
        ntsObtained.setPromptText("Obtained Score (e.g. 70)");
        ntsObtained.setStyle(fieldStyle);
        TextField ntsTotal = new TextField(currentStudent.getNtsTotal() > 0 ? String.valueOf((int)currentStudent.getNtsTotal()) : "100");
        ntsTotal.setPromptText("Total Marks (e.g. 100)");
        ntsTotal.setStyle(fieldStyle);
        HBox ntsRow = new HBox(10, ntsObtained, new Label("out of"), ntsTotal);
        ntsRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // ── NET (NUST only) ──
        Label netHeader = new Label("NUST NET Score (for NUST only — out of 200)");
        netHeader.setStyle(labelStyle);
        netHeader.setTextFill(Color.DARKRED);
        TextField netObtained = new TextField(currentStudent.getNetMarks() > 0 ? String.valueOf((int)currentStudent.getNetMarks()) : "");
        netObtained.setPromptText("NET Score (e.g. 150)");
        netObtained.setStyle(fieldStyle);
        TextField netTotal = new TextField(currentStudent.getNetTotal() > 0 ? String.valueOf((int)currentStudent.getNetTotal()) : "200");
        netTotal.setPromptText("Total (e.g. 200)");
        netTotal.setStyle(fieldStyle);
        HBox netRow = new HBox(10, netObtained, new Label("out of"), netTotal);
        netRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // ── Final Aggregate ──
        Label aggregateHeader = new Label("Final Aggregate:");
        aggregateHeader.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Label finalAgg = new Label("Final Aggregate: –");
        finalAgg.setFont(Font.font("Arial", 13));
        finalAgg.setTextFill(Color.DARKBLUE);

        Label nustStatus = new Label("NUST Eligibility: –");
        nustStatus.setFont(Font.font("Arial", 13));
        nustStatus.setTextFill(Color.DARKRED);

        Runnable updateFinalAgg = () -> {
            try {
                double mo = matricObtained.getText().isEmpty() ? 0 : Double.parseDouble(matricObtained.getText());
                double mt = matricTotal.getText().isEmpty() ? 1100 : Double.parseDouble(matricTotal.getText());
                double fo = fscObtained.getText().isEmpty() ? 0 : Double.parseDouble(fscObtained.getText());
                double ft = fscTotal.getText().isEmpty() ? 1100 : Double.parseDouble(fscTotal.getText());
                double no = ntsObtained.getText().isEmpty() ? 0 : Double.parseDouble(ntsObtained.getText());
                double nt = ntsTotal.getText().isEmpty() ? 100 : Double.parseDouble(ntsTotal.getText());
                double neo = netObtained.getText().isEmpty() ? 0 : Double.parseDouble(netObtained.getText());
                double net = netTotal.getText().isEmpty() ? 200 : Double.parseDouble(netTotal.getText());

                double mp = mt > 0 ? (mo / mt) * 100 : 0;
                double fp = ft > 0 ? (fo / ft) * 100 : 0;
                double np = nt > 0 ? (no / nt) * 100 : 0;
                double nep = net > 0 ? (neo / net) * 100 : 0;

                double finalValue = (neo > 0 && net > 0)
                        ? (mp * 0.10) + (fp * 0.15) + (nep * 0.75)
                        : (mp * 0.10) + (fp * 0.40) + (np * 0.50);

                finalAgg.setText(String.format("Final Aggregate: %.2f%%", finalValue));

                boolean canCheckNust = mo > 0 && mt > 0 && fo > 0 && ft > 0 && neo > 0 && net > 0;
                if (!canCheckNust) {
                   
                    nustStatus.setTextFill(Color.DARKRED);
                } else {
                    University nustUniversity = null;
                    for (University university : dataManager.getUniversities()) {
                        if (university.getMeritType() == University.MeritType.NUST) {
                            nustUniversity = university;
                            break;
                        }
                    }
                    if (nustUniversity != null && nustUniversity.isEligible(currentStudent)) {
                        nustStatus.setText("NUST Eligibility: Eligible");
                        nustStatus.setTextFill(Color.GREEN);
                    } else {
                        nustStatus.setText("NUST Eligibility: Not Eligible");
                        nustStatus.setTextFill(Color.RED);
                    }
                }
            } catch (Exception ex) { /* ignore parse errors */ }
        };

        updateFinalAgg.run();

        Button saveButton = createStyledButton("💾  Save All Marks", "#28a745");
        saveButton.setPrefWidth(300);

        saveButton.setOnAction(e -> {
            try {
                double mo = Double.parseDouble(matricObtained.getText());
                double mt = Double.parseDouble(matricTotal.getText());
                double fo = Double.parseDouble(fscObtained.getText());
                double ft = Double.parseDouble(fscTotal.getText());
                double no2 = ntsObtained.getText().isEmpty() ? 0 : Double.parseDouble(ntsObtained.getText());
                double nt2 = ntsTotal.getText().isEmpty() ? 100 : Double.parseDouble(ntsTotal.getText());
                double neo = netObtained.getText().isEmpty() ? 0 : Double.parseDouble(netObtained.getText());
                double net2 = netTotal.getText().isEmpty() ? 200 : Double.parseDouble(netTotal.getText());

                if (mo > mt || fo > ft || no2 > nt2 || neo > net2) {
                    showAlert("Error", "Obtained marks cannot exceed total marks!");
                    return;
                }

                currentStudent.setMatricMarks(mo); currentStudent.setMatricTotal(mt);
                currentStudent.setFscMarks(fo);    currentStudent.setFscTotal(ft);
                currentStudent.setNtsMarks(no2);   currentStudent.setNtsTotal(nt2);
                currentStudent.setNetMarks(neo);   currentStudent.setNetTotal(net2);

                dataManager.updateStudentMarks(currentStudent);

                updateFinalAgg.run();

                showAlert("Success", String.format(
                    "Marks saved!\n\nMatric: %.0f/%.0f (%.1f%%)\nFSC: %.0f/%.0f (%.1f%%)\nNTS: %.0f/%.0f (%.1f%%)\nNET: %.0f/%.0f (%.1f%%)\n\n%s",
                    mo, mt, currentStudent.getMatricPercent(),
                    fo, ft, currentStudent.getFscPercent(),
                    no2, nt2, currentStudent.getNtsPercent(),
                    neo, net2, currentStudent.getNetPercent(),
                    finalAgg.getText()
                ));
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numeric values for all fields!");
            }
        });

        form.getChildren().addAll(
            matricHeader, matricRow,
            fscHeader, fscRow,
            ntsHeader, ntsRow,
            netHeader, netRow,
            new javafx.scene.control.Separator(),
            aggregateHeader, finalAgg, nustStatus,
            saveButton
        );

        mainContent.getChildren().addAll(backButton, title, subtitle, form);
    }

    private void showUniversityFinder() {
        mainContent.getChildren().clear();

        Student currentStudent = dataManager.getCurrentStudent();

        // Add back button
        Button backButton = createStyledButton("← Back to Dashboard", "#6c757d");
        backButton.setOnAction(e -> returnToDashboard());

        Label title = new Label("Find Suitable Universities");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        if (!currentStudent.hasCompleteMarks()) {
            Label warningLabel = new Label("Please enter your marks first in the 'Enter Marks' section!");
            warningLabel.setTextFill(Color.RED);
            warningLabel.setFont(Font.font("Arial", 16));
            mainContent.getChildren().addAll(backButton, title, warningLabel);
            return;
        }

        List<University> eligibleUniversities = dataManager.getEligibleUniversities(currentStudent);

        VBox universitiesList = new VBox(15);

        if (eligibleUniversities.isEmpty()) {
            Label noResultsLabel = new Label("No universities match your current marks. Consider improving your scores!");
            noResultsLabel.setTextFill(Color.ORANGE);
            noResultsLabel.setFont(Font.font("Arial", 16));
            universitiesList.getChildren().add(noResultsLabel);
        } else {
            Label resultLabel = new Label("You are eligible for " + eligibleUniversities.size() + " universities:");
            resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            resultLabel.setTextFill(Color.GREEN);
            universitiesList.getChildren().add(resultLabel);

            for (University uni : eligibleUniversities) {
                VBox uniBox = createUniversityCard(uni, true, currentStudent);
                universitiesList.getChildren().add(uniBox);
            }
        }

        mainContent.getChildren().addAll(backButton, title, universitiesList);
    }

    private void showAllUniversities() {
        mainContent.getChildren().clear();

        // Add back button
        Button backButton = createStyledButton("← Back to Dashboard", "#6c757d");
        backButton.setOnAction(e -> returnToDashboard());

        Label title = new Label("All Universities");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        VBox universitiesList = new VBox(15);

        for (University uni : dataManager.getUniversities()) {
            VBox uniBox = createUniversityCard(uni, false, null);
            universitiesList.getChildren().add(uniBox);
        }

        mainContent.getChildren().addAll(backButton, title, universitiesList);
    }

    private VBox createUniversityCard(University uni, boolean showEligibility, Student student) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label nameLabel = new Label(uni.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.DARKBLUE);

        Label locationLabel = new Label("📍 " + uni.getLocation() + " (" + uni.getType() + ")");
        locationLabel.setFont(Font.font("Arial", 14));

        Label programsLabel = new Label("📚 Programs: " + uni.getPrograms());
        programsLabel.setFont(Font.font("Arial", 14));

        Label requirementsLabel = new Label(String.format("📋 Min Required: Matric ≥ %.0f%%  |  FSC ≥ %.0f%%  |  %s ≥ %.0f%%",
                uni.getMatricRequired(), uni.getFscRequired(), uni.getEntryTestName(), uni.getNtsRequired()));
        requirementsLabel.setFont(Font.font("Arial", 13));

        Label formulaLabel = new Label("🧮 Merit Formula: " + uni.getMeritFormula());
        formulaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        formulaLabel.setTextFill(Color.DARKBLUE);

        Label minAggLabel = new Label(String.format("📊 Min Aggregate to Qualify: %.0f%%", uni.getMinAggregate()));
        minAggLabel.setFont(Font.font("Arial", 12));
        minAggLabel.setTextFill(Color.GRAY);

        // Updated checkbox handling
        CheckBox applied = new CheckBox("Applied");
        applied.setSelected(isUniversityApplied(uni));
        applied.setOnAction(e -> {
            if (applied.isSelected()) {
                appliedUniversities.add(uni);
            } else {
                appliedUniversities.remove(uni);
            }
            if (appliedNumberCounter != null) {
                appliedNumberCounter.setText(String.valueOf(appliedUniversities.size()));
            }
            persistUniversitySelections();
        });

        CheckBox saved = new CheckBox("Saved");
        saved.setSelected(isUniversitySaved(uni));
        saved.setOnAction(e -> {
            if (saved.isSelected()) {
                savedUniversities.add(uni);
            } else {
                savedUniversities.remove(uni);
            }
            if (savedNumberCounter != null) {
                savedNumberCounter.setText(String.valueOf(savedUniversities.size()));
            }
            persistUniversitySelections();
        });

        HBox checkSaved = new HBox(20, applied, saved);

        card.getChildren().addAll(nameLabel, locationLabel, programsLabel, requirementsLabel, formulaLabel, minAggLabel, checkSaved);

        if (showEligibility && student != null && student.hasCompleteMarks()) {
            boolean eligible = uni.isEligible(student);
            double agg = uni.calculateAggregate(student);

            Label eligibilityLabel = new Label(eligible ? "✅ ELIGIBLE" : "❌ NOT ELIGIBLE");
            eligibilityLabel.setTextFill(eligible ? Color.GREEN : Color.RED);
            eligibilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label aggLabel = new Label(String.format("Your Aggregate: %.2f%%  (Min Required: %.0f%%)", agg, uni.getMinAggregate()));
            aggLabel.setFont(Font.font("Arial", 13));
            aggLabel.setTextFill(eligible ? Color.GREEN : Color.ORANGE);

            card.getChildren().addAll(eligibilityLabel, aggLabel);
        }

        if (uni.getMeritType() == University.MeritType.NUST) {
            Label nustStatusLabel;
            if (student == null || !student.hasCompleteMarks() || !student.hasNETMarks()) {
                nustStatusLabel = new Label("NUST Eligibility: Enter marks to check");
                nustStatusLabel.setTextFill(Color.DARKRED);
            } else {
                nustStatusLabel = new Label(uni.isEligible(student) ? "NUST Eligibility: Eligible" : "NUST Eligibility: Not Eligible");
                nustStatusLabel.setTextFill(uni.isEligible(student) ? Color.GREEN : Color.RED);
            }
            nustStatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            card.getChildren().add(nustStatusLabel);
        }

        return card;
    }

    private boolean isUniversitySaved(University university) {
        for (University saved : savedUniversities) {
            if (saved.getName().equalsIgnoreCase(university.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isUniversityApplied(University university) {
        for (University applied : appliedUniversities) {
            if (applied.getName().equalsIgnoreCase(university.getName())) {
                return true;
            }
        }
        return false;
    }

    private void persistUniversitySelections() {
        Student currentStudent = dataManager.getCurrentStudent();
        if (currentStudent == null) {
            return;
        }

        Set<String> savedNames = new HashSet<>();
        for (University university : savedUniversities) {
            savedNames.add(university.getName());
        }

        Set<String> appliedNames = new HashSet<>();
        for (University university : appliedUniversities) {
            appliedNames.add(university.getName());
        }

        dataManager.saveSavedUniversities(currentStudent, savedNames);
        dataManager.saveAppliedUniversities(currentStudent, appliedNames);
    }

    private void showExportOptions() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Export Data");
        alert.setHeaderText("Choose what to export:");

        ButtonType studentsButton = new ButtonType("Students to CSV");
        ButtonType universitiesButton = new ButtonType("Universities to CSV");
        ButtonType bothButton = new ButtonType("Both");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(studentsButton, universitiesButton, bothButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            if (response == studentsButton) {
                fileChooser.setInitialFileName("students_export.csv");
                File file = fileChooser.showSaveDialog(mainContent.getScene().getWindow());
                if (file != null) {
                    if (dataManager.exportStudentsToCSV(file.getAbsolutePath())) {
                        showAlert("Success", "Students exported successfully!");
                    } else {
                        showAlert("Error", "Failed to export students!");
                    }
                }
            } else if (response == universitiesButton) {
                fileChooser.setInitialFileName("universities_export.csv");
                File file = fileChooser.showSaveDialog(mainContent.getScene().getWindow());
                if (file != null) {
                    if (dataManager.exportUniversitiesToCSV(file.getAbsolutePath())) {
                        showAlert("Success", "Universities exported successfully!");
                    } else {
                        showAlert("Error", "Failed to export universities!");
                    }
                }
            } else if (response == bothButton) {
                File directory = new FileChooser().showSaveDialog(mainContent.getScene().getWindow());
                if (directory != null) {
                    String basePath = directory.getParent() + File.separator;
                    boolean studentsSuccess = dataManager.exportStudentsToCSV(basePath + "students_export.csv");
                    boolean universitiesSuccess = dataManager.exportUniversitiesToCSV(basePath + "universities_export.csv");

                    if (studentsSuccess && universitiesSuccess) {
                        showAlert("Success", "Both files exported successfully!");
                    } else {
                        showAlert("Partial Success", "Some files may not have been exported correctly.");
                    }
                }
            }
        });
    }

    private void handleBackup() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Backup Location");
        File directory = fileChooser.showSaveDialog(mainContent.getScene().getWindow());

        if (directory != null) {
            String backupPath = directory.getParent() + File.separator + "backup_" + System.currentTimeMillis();
            if (dataManager.backupData(backupPath)) {
                showAlert("Success", "Data backed up successfully to:\n" + backupPath);
            } else {
                showAlert("Error", "Failed to backup data!");
            }
        }
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14; -fx-background-radius: 5;", color));
        return button;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}