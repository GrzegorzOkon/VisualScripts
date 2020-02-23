package okon.VisualScripts;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import okon.VisualScripts.config.HourParamsReader;
import okon.VisualScripts.config.ScriptParamsReader;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class VisualScriptsWindow extends Application implements Observer {
    private static final Logger logger = LogManager.getLogger(VisualScriptsWindow.class);
    private Stage stage;
    private VisualScripts subject;
    TabPane tabPanel = new TabPane();
    static final String version;
    final static List<Script> scripts;
    final static List<Hour> hours;

    static {
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j2.xml");
        context.setConfigLocation(file.toURI());
        version = ProgramVersion.getTitleDescription();
        scripts = ScriptParamsReader.readScriptParams(new File("./config/scripts.xml"));
        hours = HourParamsReader.readHourParams(new File("./config/hours.xml"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Platform.setImplicitExit(false);
        subject = new VisualScripts();
        subject.addObserver(this);
        stage.setScene(prepareScene(stage));
        stage.setTitle(version);
        stage.show();
    }

    private Scene prepareScene(Stage stage) {
        BorderPane panel = prepareWindow(stage);
        prepareTabs();
        return new Scene(panel, 600, 400);
    }

    private BorderPane prepareWindow(Stage stage) {
        BorderPane windowPanel = new BorderPane();
        tabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPanel.getSelectionModel().selectedIndexProperty().addListener( (observable, oldValue, newValue) -> {
            subject.setTab(newValue.intValue());
        });
        windowPanel.setCenter(tabPanel);

        BorderPane buttonPanel = new BorderPane();
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));

        HBox buttonBox = new HBox(16);

        Button cleanButton = new Button("Clean");
        cleanButton.setPrefSize(100, 20);
        cleanButton.setOnAction((event) -> {
            subject.activateCleanButton();
        });
        buttonBox.getChildren().add(cleanButton);

        Button runButton = new Button("Run");
        runButton.setPrefSize(100, 20);
        runButton.setStyle("-fx-focus-color: red;");
        runButton.setOnAction((event) -> {
            subject.activateRunButton();
        });
        buttonBox.getChildren().add(runButton);

        Button closeButton = new Button("Close");
        closeButton.setPrefSize(100, 20);
        closeButton.setOnAction((event) -> {
            Platform.exit();
            //stage.close();
        });

        buttonPanel.setLeft(buttonBox);
        buttonPanel.setRight(closeButton);
        windowPanel.setBottom(buttonPanel);
        return windowPanel;
    }

    private void prepareTabs() {
        prepareScriptsTab();
        prepareHoursTab();
        prepareSchedulerTab();
    }

    private void prepareScriptsTab() {
        Tab tab = new Tab("Scripts");
        tabPanel.getTabs().add(tab);

        FlowPane flow = new FlowPane(Orientation.VERTICAL);
        flow.setStyle("-fx-border-width: 1px, 0px;" +
                "-fx-border-color: lightgrey lightgrey lightgrey lightgrey;" +
                "-fx-border-insets: 10px;");
        flow.setPadding(new Insets(30, 30, 30, 30));
        flow.setVgap(15);
        flow.setHgap(90);

        for (int i = 0; i < scripts.size(); i++) {
            final int index = i;
            CheckBox checkBox = new CheckBox(scripts.get(index).getAlias());
            checkBox.setOnMouseClicked((event) -> {
                subject.setScript(index, checkBox.isSelected());
            });
            flow.getChildren().add(checkBox);
        }
        tab.setContent(flow);
    }

    private void prepareHoursTab() {
        Tab tab = new Tab("Hours");
        tabPanel.getTabs().add(tab);

        FlowPane flow = new FlowPane(Orientation.VERTICAL);
        flow.setPadding(new Insets(30, 30, 30, 30));
        flow.setStyle("-fx-border-width: 1px, 0px;" +
                "-fx-border-color: lightgrey lightgrey lightgrey lightgrey;" +
                "-fx-border-insets: 10px;");
        flow.setVgap(15);
        flow.setHgap(90);

        ToggleGroup toggleGroup = new ToggleGroup();
        for (int i = 0; i < hours.size(); i++) {
            final int index = i;
            RadioButton radioButton = new RadioButton(hours.get(index).getAlias());
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setOnMouseClicked((event) -> {
                subject.setHour(index);
            });
            flow.getChildren().add(radioButton);
        }
        tab.setContent(flow);
    }

    private void prepareSchedulerTab() {
        Tab tab = new Tab("Scheduler");
        tabPanel.getTabs().add(tab);

        BorderPane panel = new BorderPane();
        panel.setStyle("-fx-border-width: 1px, 0px;" +
                "-fx-border-color: lightgrey lightgrey lightgrey lightgrey;" +
                "-fx-border-insets: 10px;");

        FlowPane verticalFlow = new FlowPane(Orientation.VERTICAL);
        verticalFlow.setPadding(new Insets(30, 30, 0, 30));
        verticalFlow.setVgap(15);
        verticalFlow.setHgap(90);

        for (int i = 0; i < hours.size(); i++) {
            final int index = i;
            CheckBox checkBox = new CheckBox(hours.get(index).getAlias());
            checkBox.setOnMouseClicked((event) -> {
                subject.setScheduler(index, checkBox.isSelected());
            });
            verticalFlow.getChildren().add(checkBox);
        }

        FlowPane horizontalFlow = new FlowPane(Orientation.HORIZONTAL);
        horizontalFlow.setPadding(new Insets(15, 15, 15, 15));
        horizontalFlow.setVgap(15);
        horizontalFlow.setHgap(15);

        CheckBox evenCheckBox = new CheckBox("Even");
        evenCheckBox.setFont(new Font(10));
        evenCheckBox.setOnMouseClicked((event) -> {
            subject.setEven(evenCheckBox.isSelected());
        });
        horizontalFlow.getChildren().add(evenCheckBox);

        CheckBox oddCheckBox = new CheckBox("Odd");
        oddCheckBox.setFont(new Font(10));
        oddCheckBox.setOnMouseClicked((event) -> {
            subject.setOdd(oddCheckBox.isSelected());
        });
        horizontalFlow.getChildren().add(oddCheckBox);

        panel.setCenter(verticalFlow);
        panel.setBottom(horizontalFlow);
        tab.setContent(panel);
    }

    private void addAppToTray() {
        try {
            java.awt.Toolkit.getDefaultToolkit();

            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = new URL("file:image/horse.png");
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
            trayIcon.setToolTip("BlackHorse");
            trayIcon.setImageAutoSize(true);

            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);

            java.awt.MenuItem openItem = new java.awt.MenuItem("Open");
            openItem.addActionListener(event -> {
                Platform.runLater(this::showStage);
                tray.remove(trayIcon);
            });
            openItem.setFont(boldFont);

            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
            });
            exitItem.setFont(boldFont);

            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private void hideStage() {
        if (stage != null) {
            stage.hide();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (isScriptOnFirstTabChanged(arg)) {
            Node node = getScriptsFromFirstTabContent().get(((int[])arg)[1]);
            if (node instanceof CheckBox) {
                ((CheckBox)node).setSelected(subject.getScript(((int[])arg)[1]));
            }
        } else if (isHourOnSecondTabChanged(arg)) {
            Node node = getHoursFromSecondTabContent().get(((int[])arg)[1]);
            if (node instanceof RadioButton) {
                ((RadioButton)node).setSelected(false);
            }
        } else if (isSchedulerOnThirdTabChanged(arg)) {
            Node node = getSchedulersFromThirdTabContent().get(((int[])arg)[1]);
            if (node instanceof CheckBox) {
                ((CheckBox)node).setSelected(subject.getScheduler(((int[])arg)[1]));
            }
        } else if (isOptionOnThirdTabChanged(arg)) {
            Node node = getOptionsFromThirdTabContent().get(((int[])arg)[1]);
            if (node instanceof CheckBox) {
                ((CheckBox)node).setSelected(subject.getOption(((int[])arg)[1]));
            }
        } else if (isRequestForAddingToTray(arg)) {
            javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
            hideStage();
        }
    }

    private boolean isScriptOnFirstTabChanged(Object arg) { return (arg instanceof int[] && ((int[])arg)[0] == 0); }

    private boolean isHourOnSecondTabChanged(Object arg) {
        return (arg instanceof int[] && ((int[])arg)[0] == 1);
    }

    private boolean isSchedulerOnThirdTabChanged(Object arg) {
        return (arg instanceof int[] && ((int[])arg)[0] == 2);
    }

    private boolean isOptionOnThirdTabChanged(Object arg) { return (arg instanceof int[] && ((int[])arg)[0] == 3); }

    private boolean isRequestForAddingToTray(Object arg) { return (arg instanceof int[] && ((int[])arg)[0] == 4); }

    private List<Node> getScriptsFromFirstTabContent() {
        return ((FlowPane)tabPanel.getTabs().get(0).getContent()).getChildren();
    }

    private List<Node> getHoursFromSecondTabContent() {
        return ((FlowPane)tabPanel.getTabs().get(1).getContent()).getChildren();
    }

    private List<Node> getSchedulersFromThirdTabContent() {
        return ((FlowPane)((BorderPane)tabPanel.getTabs().get(2).getContent()).getCenter()).getChildren();
    }

    private List<Node> getOptionsFromThirdTabContent() {
        return ((FlowPane)((BorderPane)tabPanel.getTabs().get(2).getContent()).getBottom()).getChildren();
    }
}