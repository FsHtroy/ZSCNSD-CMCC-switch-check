package com.htroy.cmccportcheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class Controller implements Initializable {

    @FXML
    private HBox oddPortBtnHBox;
    @FXML
    private HBox evenPortBtnHBox;
    @FXML
    private HBox resShowLabelHBox;
    @FXML
    private TextField switchSnText;
    @FXML
    private TextField switchFloorText;
    @FXML
    private Label informationLabel;
    @FXML
    private ToggleGroup group;
    @FXML
    private RadioButton ABbRBtn;
    @FXML
    private RadioButton CDbRBtn;

    private ToggleGroup portTgGroup = new ToggleGroup();
    private RadioButton[] portRadioBtnGroup = new RadioButton[24];
    private Label[] portStatus = new Label[24];
    private String frontSn = "48575443";
    private Connection con = null;
    private int[] portCheckRs = new int[24];

    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 0;i < 24;i++) {
            portRadioBtnGroup[i] = new RadioButton(i + 1 + "");
            setPortRBtn(portRadioBtnGroup[i]);
            if (i % 2 == 1) {
                evenPortBtnHBox.getChildren().add(portRadioBtnGroup[i]);
            } else {
                oddPortBtnHBox.getChildren().add(portRadioBtnGroup[i]);
            }
            portStatus[i] = new Label(portRadioBtnGroup[i].getText());
            portStatus[i].setStyle("-fx-background-color: #FFFF00");
        }
        switchSnText.setText(frontSn);
        resShowLabelHBox.getChildren().addAll(portStatus);
        ABbRBtn.setUserData("AB");
        CDbRBtn.setUserData("CD");
    }

    private void setPortRBtn(RadioButton btn) {
        btn.setToggleGroup(portTgGroup);
        btn.setPrefWidth(40);
        btn.setUserData(btn.getText());
        btn.setOnAction((ActionEvent t) -> {
            checkSelectPort();
        });
    }

    @FXML
    protected void checkSelectPort() {
        String portName = portTgGroup.getSelectedToggle().getUserData().toString();
        int portNum = Integer.parseInt(portName) - 1;
        portCheckRs[portNum] = NetworkChecker.isError651();
        if (portCheckRs[portNum] == 1) {
            portStatus[portNum].setStyle("-fx-background-color: #00FF00");
            informationLabel.setText(portName + "号口正常");
        } else {
            portStatus[portNum].setStyle("-fx-background-color: #EE0000");
            informationLabel.setText(portName + "号口故障");
        }
    }

    private void setDBCon() {
        if (con == null) {
            try {
                con = MysqlConnection.getConnection("#dbuser", "#dbpasswd"); // Please change the db auth
                informationLabel.setText("连接数据库成功");
            } catch (Exception e) {
                e.printStackTrace();
                informationLabel.setText("连接数据库失败");
            }
        }
    }

    @FXML
    protected void reConnect() {
        con = null;
        setDBCon();
    }

    @FXML
    protected void uploadTestRs() {
        setDBCon();
        if (switchSnText.getText().length() != 16) {
            informationLabel.setText("SN码长度不正确！");
            return;
        }
        if (switchFloorText.getText().length() == 0) {
            informationLabel.setText("楼层号未填写！");
            return;
        }
        try {
            String upSql = "";
            Statement stmt = con.createStatement();
            String switchName = switchSnText.getText() + "@" +group.getSelectedToggle().getUserData().toString() + switchFloorText.getText();
            //File rsDirCreate = new File("resault");
            //rsDirCreate.mkdir();
            //PrintWriter rsFile = new PrintWriter("resault/" + switchName + ".txt", "UTF-8");
            //rsFile.print(switchName + "\t");
            for (int i = 0;i < 24;i++) {
                boolean isCheck = false;
                int port = i + 1;
                if (portCheckRs[i] == 1) {
                    upSql = "INSERT INTO `switcher` (`switchsn`, `" + port +"`) VALUES ('" + switchName + "', '1') ON DUPLICATE KEY UPDATE `" + port + "` = '1'";
                    //rsFile.print("1\t");
                    isCheck = true;
                } else if (portCheckRs[i] == 2) {
                    upSql = "INSERT INTO `switcher` (`switchsn`, `" + port +"`) VALUES ('" + switchName + "', '0') ON DUPLICATE KEY UPDATE `" + port + "` = '0'";
                    //rsFile.print("0\t");
                    isCheck = true;
                }
                if (isCheck) {
                    stmt.executeUpdate(upSql);
                    portStatus[i].setStyle("-fx-background-color: #FFFF00");
                    informationLabel.setText(port + "端口结果已保存");
                    portCheckRs[i] = 0;
                } else {
                    //rsFile.print("NULL\t");
                }
            }
            //rsFile.println();
            //rsFile.close();
            switchSnText.setText(frontSn);
            informationLabel.setText(informationLabel.getText() + "，请测试下一台交换机");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            informationLabel.setText("数据库出错，请尝试重连!");
        }
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //    informationLabel.setText("文件打开出错");
        //} catch (UnsupportedEncodingException e) {
        //    e.printStackTrace();
        //    informationLabel.setText("文件打开出错");
        //}
    }

}
