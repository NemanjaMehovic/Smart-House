/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavenkorisnickiuredjaj;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author neman
 */
public class KorisnickiUredjaj extends javax.swing.JFrame {

    
    private static String Username;
    private static String Password;
    private static JFrame MainFrame;
    
    private static String credentials(String username, String password)
    {
        String tmp = username+":"+password;
        byte[] bytes = Base64.encodeBase64(tmp.getBytes());
        return "Basic "+ new String(bytes);
    }
    
    private static void switchPanel(JPanel panel, String name)
    {
        CardLayout card = (CardLayout)panel.getParent().getLayout();
        card.show(panel.getParent(), name);
    }
    
    private static void clearText(JPanel panel)
    {
        for(Component component : panel.getComponents())
            if(component instanceof JTextField)
                ((JTextField) component).setText("");
    }
    
    private void setEvrythingUp()
    {
        try
        {
            Obligations.clearObligationsList();
            Alarms.clearAlarmsList();
            HttpEntity r_entity = Request.Get("http://localhost:8080/MavenKorisnickiServis/REST/Obligation").addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password)).execute().returnResponse().getEntity();
            String obligationsAnswer = EntityUtils.toString(r_entity);
            StringTokenizer objectTokenizer = new StringTokenizer(obligationsAnswer, System.lineSeparator());
            int size = objectTokenizer.countTokens();
            for(int i = 0; i < size; i++)
            {
                StringTokenizer attributeTokenizer = new StringTokenizer(objectTokenizer.nextToken(), "%");
                int id = Integer.parseInt(attributeTokenizer.nextToken());
                String name = attributeTokenizer.nextToken();
                String date = attributeTokenizer.nextToken();
                String country = attributeTokenizer.nextToken();
                String zip = attributeTokenizer.nextToken();
                String city = attributeTokenizer.nextToken();
                String street = attributeTokenizer.nextToken();
                String duration = attributeTokenizer.nextToken();
                String songName = "";
                if(attributeTokenizer.countTokens()>0)
                    songName = attributeTokenizer.nextToken();
                attributeTokenizer = new StringTokenizer(duration, ":");
                int durationH = Integer.parseInt(attributeTokenizer.nextToken());
                int durationM = Integer.parseInt(attributeTokenizer.nextToken());
                int durationS = Integer.parseInt(attributeTokenizer.nextToken());
                new Obligations(id, name, date, durationH, durationM, durationS, country, city, zip, street, songName);
            }
            DefaultListModel<String> model = new DefaultListModel<String>();
            for(Obligations i : Obligations.obligations)
                model.addElement(i.toString());
            ObligationList.setModel(model);
            r_entity = Request.Get("http://localhost:8080/MavenKorisnickiServis/REST/Alarm").addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password)).execute().returnResponse().getEntity();
            String alarmAnswer = EntityUtils.toString(r_entity);
            objectTokenizer = new StringTokenizer(alarmAnswer, System.lineSeparator());
            size = objectTokenizer.countTokens();
            for(int i = 0; i < size; i++)
            {
                StringTokenizer attributeTokenizer = new StringTokenizer(objectTokenizer.nextToken(), "%");
                int id = Integer.parseInt(attributeTokenizer.nextToken());
                String date = attributeTokenizer.nextToken();
                String repeatS = attributeTokenizer.nextToken();
                int reapet = Integer.parseInt(repeatS);
                String songName = attributeTokenizer.nextToken();
                new Alarms(id, date, reapet, songName);
            }
            model = new DefaultListModel<String>();
            for(Alarms i : Alarms.alarms)
                model.addElement(i.toString());
            AlarmsList.setModel(model);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void logOut()
    {
        ObligationNotificationLabel.setText("");
        ObligationNotificationLabel.setForeground(new Color(255, 0, 0));
        clearText(jPanel1);
        ObligationHSpinner.setValue(new Integer(0));
        ObligationMSpinner.setValue(new Integer(0));
        ObligationSSpinner.setValue(new Integer(0));
        ObligationAlarm.setSelected(true);
        ObligationHasDestination.setSelected(true);
        clearText(jPanel5);
        AlarmRepeatCheckBox.setSelected(true);
        AlarmNotificationLabel.setText("");
        AlarmNotificationLabel.setForeground(new Color(255, 0, 0));
        Username = "";
        Password = "";
        Alarms.clearAlarmsList();
        Obligations.clearObligationsList();
        MusicNotificationLabel.setForeground(new Color(255, 0, 0));
        MusicNotificationLabel.setText("");
        MusicSongNameTextBox.setText("");
        CalculatorCheckBox.setSelected(false);
        clearText(jPanel8);
        clearText(jPanel9);
        CalculatorLabel.setForeground(new Color(0, 0, 0));
        CalculatorLabel.setText("Trip duration:");
        switchPanel(RegisterPanel, "LogInPanel");
    }
    /**
     * Creates new form KorisnickiUredjaj
     */
    public KorisnickiUredjaj() {
        initComponents();
        setLocationRelativeTo(null);
        MainFrame = this;
        ObligationList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(ObligationList.getSelectedIndex() < 0)
                    return;
                int index = ObligationList.getSelectedIndex();
                ObligationNameTextBox.setText(Obligations.obligations.get(index).getName());
                ObligationDateTextBox.setText(Obligations.obligations.get(index).getDate());
                ObligationHSpinner.setValue(new Integer(Obligations.obligations.get(index).getDurationH()));
                ObligationMSpinner.setValue(new Integer(Obligations.obligations.get(index).getDurationM()));
                ObligationSSpinner.setValue(new Integer(Obligations.obligations.get(index).getDurationS()));
                ObligationHasDestination.setSelected(true);
                ObligationCountryTextBox.setText(Obligations.obligations.get(index).getCountry());
                ObligationCityTextBox.setText(Obligations.obligations.get(index).getCity());
                ObligationZipTextBox.setText(Obligations.obligations.get(index).getZip());
                ObligationStreetTextBox.setText(Obligations.obligations.get(index).getStreet());
                ObligationSongNameTextBox.setText(Obligations.obligations.get(index).getSongName());
                ObligationAlarm.setSelected(!Obligations.obligations.get(index).getSongName().equals(""));
            }
        });
        AlarmsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(AlarmsList.getSelectedIndex() < 0)
                    return;
                int index = AlarmsList.getSelectedIndex();
                AlarmDateTextBox.setText(Alarms.alarms.get(index).getDate());
                AlarmSongNameTextBox.setText(Alarms.alarms.get(index).getSongName());
                AlarmRepeatCheckBox.setSelected(Alarms.alarms.get(index).getRepeat()==1);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        LogInPanel = new javax.swing.JPanel();
        LogInButton = new javax.swing.JButton();
        RegisterButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        UserNameLogInTextBox = new javax.swing.JTextField();
        PasswordLogInTextBox = new javax.swing.JTextField();
        FailedToLogInLabel = new javax.swing.JLabel();
        RegisterPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        RegisterUsernameTextBox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        RegisterPasswordTextBox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        RegisterNameTextBox = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        RegisterSurnameTextBox = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        RegisterCountryTextBox = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        RegisterCityTextBox = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        RegisterZipTextBox = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        RegisterStreetTextBox = new javax.swing.JTextField();
        RegistrationButtonPanel = new javax.swing.JPanel();
        RegisterCancelButton = new javax.swing.JButton();
        RegisterCreateButton = new javax.swing.JButton();
        RegisterFailedLabel = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        ObligationPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        ObligationNameTextBox = new javax.swing.JTextField();
        ObligationHasDestination = new javax.swing.JCheckBox();
        ObligationAlarm = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        ObligationDateTextBox = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ObligationCountryTextBox = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        ObligationCityTextBox = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        ObligationZipTextBox = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        ObligationStreetTextBox = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        ObligationHSpinner = new javax.swing.JSpinner();
        ObligationMSpinner = new javax.swing.JSpinner();
        ObligationSSpinner = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        ObligationSongNameTextBox = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        ObligationCreateButton = new javax.swing.JButton();
        ObligationUpdateButton = new javax.swing.JButton();
        ObligationDeleteButton = new javax.swing.JButton();
        ObligationNotificationLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ObligationList = new javax.swing.JList<>();
        ObligationLogOutButton = new javax.swing.JButton();
        AlarmPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        AlarmDateTextBox = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        AlarmSongNameTextBox = new javax.swing.JTextField();
        AlarmRepeatCheckBox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        AlarmCreateButton = new javax.swing.JButton();
        AlarmUpdateButton = new javax.swing.JButton();
        AlarmDeleteButton = new javax.swing.JButton();
        AlarmNotificationLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        AlarmsList = new javax.swing.JList<>();
        AlarmLogOutButton = new javax.swing.JButton();
        MusicPanel = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        MusicSongNameTextBox = new javax.swing.JTextField();
        MusicPlayButto = new javax.swing.JButton();
        MusicGetAllPlayed = new javax.swing.JButton();
        MusicNotificationLabel = new javax.swing.JLabel();
        CalculatorPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        CalculatorSCountryTextBox = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        CalculatorSCityTextBox = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        CalculatorSZipTextBox = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        CalculatorSStreetTextBox = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        CalculatorDCountryTextBox = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        CalculatorDCityTextBox = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        CalculatorDZipTextBox = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        CalculatorDStreetTextBox = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        CalculatorCheckBox = new javax.swing.JCheckBox();
        CalculatorPostButton = new javax.swing.JButton();
        CalculatorLogOut = new javax.swing.JButton();
        CalculatorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("KorisnickiUredjaj");
        setPreferredSize(new java.awt.Dimension(930, 700));
        setResizable(false);
        setSize(new java.awt.Dimension(930, 700));
        getContentPane().setLayout(new java.awt.CardLayout());

        LogInPanel.setLayout(new java.awt.GridBagLayout());

        LogInButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LogInButton.setText("LogIn");
        LogInButton.setPreferredSize(new java.awt.Dimension(250, 25));
        LogInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogInButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        LogInPanel.add(LogInButton, gridBagConstraints);

        RegisterButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        RegisterButton.setText("Register");
        RegisterButton.setPreferredSize(new java.awt.Dimension(250, 25));
        RegisterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        LogInPanel.add(RegisterButton, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        LogInPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        LogInPanel.add(jLabel2, gridBagConstraints);

        UserNameLogInTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        LogInPanel.add(UserNameLogInTextBox, gridBagConstraints);

        PasswordLogInTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        LogInPanel.add(PasswordLogInTextBox, gridBagConstraints);

        FailedToLogInLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        FailedToLogInLabel.setForeground(new java.awt.Color(255, 26, 0));
        LogInPanel.add(FailedToLogInLabel, new java.awt.GridBagConstraints());
        FailedToLogInLabel.getAccessibleContext().setAccessibleName("FailedToLogInLabel");

        getContentPane().add(LogInPanel, "LogInPanel");

        RegisterPanel.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        RegisterPanel.add(jLabel3, gridBagConstraints);

        RegisterUsernameTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        RegisterPanel.add(RegisterUsernameTextBox, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        RegisterPanel.add(jLabel4, gridBagConstraints);

        RegisterPasswordTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        RegisterPanel.add(RegisterPasswordTextBox, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        RegisterPanel.add(jLabel5, gridBagConstraints);

        RegisterNameTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        RegisterPanel.add(RegisterNameTextBox, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Surname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        RegisterPanel.add(jLabel6, gridBagConstraints);

        RegisterSurnameTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        RegisterPanel.add(RegisterSurnameTextBox, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Country");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        RegisterPanel.add(jLabel7, gridBagConstraints);

        RegisterCountryTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 9;
        RegisterPanel.add(RegisterCountryTextBox, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("City");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        RegisterPanel.add(jLabel8, gridBagConstraints);

        RegisterCityTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 11;
        RegisterPanel.add(RegisterCityTextBox, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 12;
        RegisterPanel.add(jLabel9, gridBagConstraints);

        RegisterZipTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 13;
        RegisterPanel.add(RegisterZipTextBox, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Street");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 14;
        RegisterPanel.add(jLabel10, gridBagConstraints);

        RegisterStreetTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 15;
        RegisterPanel.add(RegisterStreetTextBox, gridBagConstraints);

        RegistrationButtonPanel.setLayout(new java.awt.GridBagLayout());

        RegisterCancelButton.setText("Back to login");
        RegisterCancelButton.setPreferredSize(new java.awt.Dimension(125, 25));
        RegisterCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterCancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(38, 17, 39, 18);
        RegistrationButtonPanel.add(RegisterCancelButton, gridBagConstraints);

        RegisterCreateButton.setText("Create");
        RegisterCreateButton.setPreferredSize(new java.awt.Dimension(125, 25));
        RegisterCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterCreateButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(38, 17, 39, 18);
        RegistrationButtonPanel.add(RegisterCreateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 17;
        RegisterPanel.add(RegistrationButtonPanel, gridBagConstraints);

        RegisterFailedLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        RegisterFailedLabel.setForeground(new java.awt.Color(255, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 18;
        RegisterPanel.add(RegisterFailedLabel, gridBagConstraints);

        getContentPane().add(RegisterPanel, "RegisterPanel");

        ObligationPanel.setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Obligation name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        jPanel1.add(jLabel11, gridBagConstraints);

        ObligationNameTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel1.add(ObligationNameTextBox, gridBagConstraints);

        ObligationHasDestination.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ObligationHasDestination.setSelected(true);
        ObligationHasDestination.setText("Has a destination");
        ObligationHasDestination.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ObligationHasDestinationItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        jPanel1.add(ObligationHasDestination, gridBagConstraints);

        ObligationAlarm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ObligationAlarm.setSelected(true);
        ObligationAlarm.setText("Alarm");
        ObligationAlarm.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ObligationAlarmItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 15;
        jPanel1.add(ObligationAlarm, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("Date (yyyy-mm-dd hh:mm:ss)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel12, gridBagConstraints);

        ObligationDateTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        jPanel1.add(ObligationDateTextBox, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Duration");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        jPanel1.add(jLabel13, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Country");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        jPanel1.add(jLabel14, gridBagConstraints);

        ObligationCountryTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        jPanel1.add(ObligationCountryTextBox, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("City");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 9;
        jPanel1.add(jLabel15, gridBagConstraints);

        ObligationCityTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        jPanel1.add(ObligationCityTextBox, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 11;
        jPanel1.add(jLabel16, gridBagConstraints);

        ObligationZipTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 12;
        jPanel1.add(ObligationZipTextBox, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Street");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 13;
        jPanel1.add(jLabel17, gridBagConstraints);

        ObligationStreetTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 14;
        jPanel1.add(ObligationStreetTextBox, gridBagConstraints);

        jPanel3.setPreferredSize(new java.awt.Dimension(350, 25));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        ObligationHSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(40, 30, 40, 31);
        jPanel3.add(ObligationHSpinner, gridBagConstraints);

        ObligationMSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(40, 30, 40, 31);
        jPanel3.add(ObligationMSpinner, gridBagConstraints);

        ObligationSSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(40, 30, 40, 31);
        jPanel3.add(ObligationSSpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        jPanel1.add(jPanel3, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Song name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 16;
        jPanel1.add(jLabel18, gridBagConstraints);

        ObligationSongNameTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 17;
        jPanel1.add(ObligationSongNameTextBox, gridBagConstraints);

        jPanel4.setPreferredSize(new java.awt.Dimension(250, 75));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        ObligationCreateButton.setText("Create");
        ObligationCreateButton.setPreferredSize(new java.awt.Dimension(80, 25));
        ObligationCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ObligationCreateButtonActionPerformed(evt);
            }
        });
        jPanel4.add(ObligationCreateButton, new java.awt.GridBagConstraints());

        ObligationUpdateButton.setText("Update");
        ObligationUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));
        ObligationUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ObligationUpdateButtonActionPerformed(evt);
            }
        });
        jPanel4.add(ObligationUpdateButton, new java.awt.GridBagConstraints());

        ObligationDeleteButton.setText("Delete");
        ObligationDeleteButton.setPreferredSize(new java.awt.Dimension(80, 25));
        ObligationDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ObligationDeleteButtonActionPerformed(evt);
            }
        });
        jPanel4.add(ObligationDeleteButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 18;
        jPanel1.add(jPanel4, gridBagConstraints);

        ObligationNotificationLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ObligationNotificationLabel.setForeground(new java.awt.Color(255, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 19;
        jPanel1.add(ObligationNotificationLabel, gridBagConstraints);

        ObligationPanel.add(jPanel1);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        ObligationList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ObligationList.setPreferredSize(new java.awt.Dimension(250, 0));
        ObligationList.setVisibleRowCount(30);
        jScrollPane2.setViewportView(ObligationList);

        jPanel2.add(jScrollPane2, new java.awt.GridBagConstraints());

        ObligationLogOutButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ObligationLogOutButton.setText("Log out");
        ObligationLogOutButton.setPreferredSize(new java.awt.Dimension(100, 25));
        ObligationLogOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ObligationLogOutButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel2.add(ObligationLogOutButton, gridBagConstraints);

        ObligationPanel.add(jPanel2);

        jTabbedPane1.addTab("Obligations", ObligationPanel);

        AlarmPanel.setLayout(new java.awt.GridLayout(1, 0));

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("Date (yyyy-mm-dd hh:mm:ss)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        jPanel5.add(jLabel20, gridBagConstraints);

        AlarmDateTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel5.add(AlarmDateTextBox, gridBagConstraints);

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("Song name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        jPanel5.add(jLabel21, gridBagConstraints);

        AlarmSongNameTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        jPanel5.add(AlarmSongNameTextBox, gridBagConstraints);

        AlarmRepeatCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AlarmRepeatCheckBox.setSelected(true);
        AlarmRepeatCheckBox.setText("Repeat every day");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        jPanel5.add(AlarmRepeatCheckBox, gridBagConstraints);

        AlarmCreateButton.setText("Create");
        AlarmCreateButton.setPreferredSize(new java.awt.Dimension(80, 25));
        AlarmCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlarmCreateButtonActionPerformed(evt);
            }
        });
        jPanel7.add(AlarmCreateButton);

        AlarmUpdateButton.setText("Update");
        AlarmUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));
        AlarmUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlarmUpdateButtonActionPerformed(evt);
            }
        });
        jPanel7.add(AlarmUpdateButton);

        AlarmDeleteButton.setText("Delete");
        AlarmDeleteButton.setPreferredSize(new java.awt.Dimension(80, 25));
        AlarmDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlarmDeleteButtonActionPerformed(evt);
            }
        });
        jPanel7.add(AlarmDeleteButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        jPanel5.add(jPanel7, gridBagConstraints);

        AlarmNotificationLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        AlarmNotificationLabel.setForeground(new java.awt.Color(255, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        jPanel5.add(AlarmNotificationLabel, gridBagConstraints);

        AlarmPanel.add(jPanel5);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        AlarmsList.setVisibleRowCount(30);
        jScrollPane1.setViewportView(AlarmsList);

        jPanel6.add(jScrollPane1, new java.awt.GridBagConstraints());

        AlarmLogOutButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AlarmLogOutButton.setText("Log out");
        AlarmLogOutButton.setPreferredSize(new java.awt.Dimension(100, 25));
        AlarmLogOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlarmLogOutButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel6.add(AlarmLogOutButton, gridBagConstraints);

        AlarmPanel.add(jPanel6);

        jTabbedPane1.addTab("Alarms", AlarmPanel);

        MusicPanel.setLayout(new java.awt.GridBagLayout());

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("Song name");
        MusicPanel.add(jLabel22, new java.awt.GridBagConstraints());

        MusicSongNameTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        MusicPanel.add(MusicSongNameTextBox, gridBagConstraints);

        MusicPlayButto.setText("Play");
        MusicPlayButto.setPreferredSize(new java.awt.Dimension(100, 25));
        MusicPlayButto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MusicPlayButtoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        MusicPanel.add(MusicPlayButto, gridBagConstraints);

        MusicGetAllPlayed.setText("Get all played");
        MusicGetAllPlayed.setPreferredSize(new java.awt.Dimension(100, 25));
        MusicGetAllPlayed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MusicGetAllPlayedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        MusicPanel.add(MusicGetAllPlayed, gridBagConstraints);

        MusicNotificationLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        MusicNotificationLabel.setForeground(new java.awt.Color(255, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        MusicPanel.add(MusicNotificationLabel, gridBagConstraints);

        jTabbedPane1.addTab("Music", MusicPanel);

        CalculatorPanel.setLayout(new java.awt.GridLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Country");
        jPanel8.add(jLabel19, new java.awt.GridBagConstraints());

        CalculatorSCountryTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel8.add(CalculatorSCountryTextBox, gridBagConstraints);

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("City");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        jPanel8.add(jLabel23, gridBagConstraints);

        CalculatorSCityTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        jPanel8.add(CalculatorSCityTextBox, gridBagConstraints);

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        jPanel8.add(jLabel24, gridBagConstraints);

        CalculatorSZipTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        jPanel8.add(CalculatorSZipTextBox, gridBagConstraints);

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Street");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        jPanel8.add(jLabel25, gridBagConstraints);

        CalculatorSStreetTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        jPanel8.add(CalculatorSStreetTextBox, gridBagConstraints);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Starting location");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        jPanel8.add(jLabel26, gridBagConstraints);

        CalculatorPanel.add(jPanel8);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Country");
        jPanel9.add(jLabel27, new java.awt.GridBagConstraints());

        CalculatorDCountryTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel9.add(CalculatorDCountryTextBox, gridBagConstraints);

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("City");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        jPanel9.add(jLabel28, gridBagConstraints);

        CalculatorDCityTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        jPanel9.add(CalculatorDCityTextBox, gridBagConstraints);

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        jPanel9.add(jLabel29, gridBagConstraints);

        CalculatorDZipTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        jPanel9.add(CalculatorDZipTextBox, gridBagConstraints);

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setText("Street");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        jPanel9.add(jLabel30, gridBagConstraints);

        CalculatorDStreetTextBox.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        jPanel9.add(CalculatorDStreetTextBox, gridBagConstraints);

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Destination");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        jPanel9.add(jLabel31, gridBagConstraints);

        CalculatorPanel.add(jPanel9);

        jPanel10.setLayout(new java.awt.GridBagLayout());

        CalculatorCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        CalculatorCheckBox.setLabel("Use current location");
        CalculatorCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CalculatorCheckBoxItemStateChanged(evt);
            }
        });
        jPanel10.add(CalculatorCheckBox, new java.awt.GridBagConstraints());

        CalculatorPostButton.setText("Calculate");
        CalculatorPostButton.setPreferredSize(new java.awt.Dimension(150, 25));
        CalculatorPostButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CalculatorPostButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel10.add(CalculatorPostButton, gridBagConstraints);

        CalculatorLogOut.setText("Log out");
        CalculatorLogOut.setPreferredSize(new java.awt.Dimension(150, 25));
        CalculatorLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CalculatorLogOutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        jPanel10.add(CalculatorLogOut, gridBagConstraints);

        CalculatorLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        CalculatorLabel.setText("Trip duration:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        jPanel10.add(CalculatorLabel, gridBagConstraints);

        CalculatorPanel.add(jPanel10);

        jTabbedPane1.addTab("Calculator", CalculatorPanel);

        getContentPane().add(jTabbedPane1, "Tabs");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LogInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogInButtonActionPerformed
        String password = PasswordLogInTextBox.getText();
        String username = UserNameLogInTextBox.getText();
        if(username.equals(""))
        {
            FailedToLogInLabel.setText("Username is mandatory");
            return;
        }
        if(password.equals(""))
        {
            FailedToLogInLabel.setText("Password is mandatory");
            return;
        }
        int status = 0;
        try
        {
            HttpResponse response = Request.Get("http://localhost:8080/MavenKorisnickiServis/REST/User/logIn").addHeader(HttpHeaders.AUTHORIZATION, credentials(username, password)).execute().returnResponse();
            
            StatusLine tmp = response.getStatusLine();
            status = tmp.getStatusCode();
            if(status != 200)
            {
                System.out.println("We failed boys "+status);
                FailedToLogInLabel.setText("Failed to log in");
            }
            else
            {
                System.out.println("We did it boys "+status);
                Username = username;
                Password = password;
                clearText(LogInPanel);
                FailedToLogInLabel.setText("");
                setEvrythingUp();
                switchPanel(LogInPanel, "Tabs");
            }
        }
        catch(Exception e)
        {
            status = 0;
            e.printStackTrace();
        }      
    }//GEN-LAST:event_LogInButtonActionPerformed

    private void RegisterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterButtonActionPerformed
        FailedToLogInLabel.setText("");
        clearText(RegisterPanel);
        switchPanel(LogInPanel, "RegisterPanel");
    }//GEN-LAST:event_RegisterButtonActionPerformed

    private void RegisterCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterCancelButtonActionPerformed
        RegisterFailedLabel.setText("");
        RegisterFailedLabel.setForeground(new Color(255, 0, 0));
        clearText(LogInPanel);
        switchPanel(RegisterPanel, "LogInPanel");
    }//GEN-LAST:event_RegisterCancelButtonActionPerformed

    private void RegisterCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterCreateButtonActionPerformed
        RegisterFailedLabel.setForeground(new Color(255, 0, 0));
        for(Component component : RegisterPanel.getComponents())
            if(component instanceof JTextField)
                if(((JTextField)component).getText().equals(""))
                {
                    RegisterFailedLabel.setText("All text field are required");
                    return;
                }
        String username = RegisterUsernameTextBox.getText();
        String password = RegisterPasswordTextBox.getText();
        String name = RegisterNameTextBox.getText();
        String surname = RegisterSurnameTextBox.getText();
        String country = RegisterCountryTextBox.getText();
        String city = RegisterCityTextBox.getText();
        String zip = RegisterZipTextBox.getText();
        String street = RegisterStreetTextBox.getText();
        int status = 0;
        try
        {
            status = Request.Post("http://localhost:8080/MavenKorisnickiServis/REST/User").bodyForm(Form.form()
                    .add("username", username)
                    .add("password", password)
                    .add("name", name)
                    .add("surname", surname)
                    .add("country", country)
                    .add("city", city)
                    .add("zip", zip)
                    .add("street", street)
                    .build()).execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                RegisterFailedLabel.setText("User successfully created");
                RegisterFailedLabel.setForeground(new Color(0, 200, 0));
            }
            else if(status == 409)
                RegisterFailedLabel.setText("Username taken");
            else
                RegisterFailedLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            RegisterFailedLabel.setText("Something went wrong when trying to create a user.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_RegisterCreateButtonActionPerformed

    private void ObligationLogOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ObligationLogOutButtonActionPerformed
        logOut();
    }//GEN-LAST:event_ObligationLogOutButtonActionPerformed

    private void ObligationHasDestinationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ObligationHasDestinationItemStateChanged
        ObligationCountryTextBox.setEnabled(ObligationHasDestination.isSelected());
        ObligationCityTextBox.setEnabled(ObligationHasDestination.isSelected());
        ObligationZipTextBox.setEnabled(ObligationHasDestination.isSelected());
        ObligationStreetTextBox.setEnabled(ObligationHasDestination.isSelected());
    }//GEN-LAST:event_ObligationHasDestinationItemStateChanged

    private void ObligationAlarmItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ObligationAlarmItemStateChanged
        ObligationSongNameTextBox.setEnabled(ObligationAlarm.isSelected());
    }//GEN-LAST:event_ObligationAlarmItemStateChanged

    private void ObligationCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ObligationCreateButtonActionPerformed
        ObligationNotificationLabel.setForeground(new Color(255,0,0));
        String name = ObligationNameTextBox.getText();
        String dateS = ObligationDateTextBox.getText();
        if(name.equals("") || dateS.equals(""))
        {
            ObligationNotificationLabel.setText("The first two text boxes are mandatory");
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try{
        Date date = formatter.parse(dateS);
        }catch(Exception e)
        {
            ObligationNotificationLabel.setText("Date format is incorrect");
            return;
        }
        String country = ObligationCountryTextBox.getText();
        String city = ObligationCityTextBox.getText();
        String zip = ObligationZipTextBox.getText();
        String street = ObligationStreetTextBox.getText();
        if(ObligationHasDestination.isSelected() && (country.equals("") || city.equals("") || zip.equals("") || street.equals("")))
        {
            ObligationNotificationLabel.setText("Please fill in the destination text boxes");
            return;
        }
        String songName = ObligationSongNameTextBox.getText();
        if(ObligationAlarm.isSelected() && songName.equals(""))
        {
            ObligationNotificationLabel.setText("Please enter a song name");
            return;
        }
        
        String duration = Integer.toString((3600*(Integer)ObligationHSpinner.getValue())+ (60*(Integer)ObligationMSpinner.getValue()) + (Integer)ObligationSSpinner.getValue());
        int status = 0;
        try
        {
            status = Request.Post("http://localhost:8080/MavenKorisnickiServis/REST/Obligation/createObligation")
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .bodyForm(Form.form()
                    .add("date", dateS)
                    .add("obligationName", name)
                    .add("duration", duration)
                    .add("hasAddress", Boolean.toString(ObligationHasDestination.isSelected()))
                    .add("hasAlarm", Boolean.toString(ObligationAlarm.isSelected()))
                    .add("country", country)
                    .add("city", city)
                    .add("zip", zip)
                    .add("street", street)
                    .add("songName", songName)
                    .build()).execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                setEvrythingUp();
                ObligationNotificationLabel.setText("Obligation successfully created");
                ObligationNotificationLabel.setForeground(new Color(0, 200, 0));
                ObligationList.setSelectedIndex(-1);
            }
            else if(status == 403)
                ObligationNotificationLabel.setText("Can't fit");
            else
                ObligationNotificationLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            ObligationNotificationLabel.setText("Something went wrong when trying to create an obligation.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_ObligationCreateButtonActionPerformed

    private void ObligationUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ObligationUpdateButtonActionPerformed
        ObligationNotificationLabel.setForeground(new Color(255,0,0));
        int index = ObligationList.getSelectedIndex();
        if(index < 0)
        {
            ObligationNotificationLabel.setText("Please select an obligation");
            return;
        }
        String name = ObligationNameTextBox.getText();
        String dateS = ObligationDateTextBox.getText();
        if(name.equals("") || dateS.equals(""))
        {
            ObligationNotificationLabel.setText("The first two text boxes are mandatory");
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try{
        Date date = formatter.parse(dateS);
        }catch(Exception e)
        {
            ObligationNotificationLabel.setText("Date format is incorrect");
            return;
        }
        String country = ObligationCountryTextBox.getText();
        String city = ObligationCityTextBox.getText();
        String zip = ObligationZipTextBox.getText();
        String street = ObligationStreetTextBox.getText();
        if(ObligationHasDestination.isSelected() && (country.equals("") || city.equals("") || zip.equals("") || street.equals("")))
        {
            ObligationNotificationLabel.setText("Please fill in the destination text boxes");
            return;
        }
        String songName = ObligationSongNameTextBox.getText();
        if(ObligationAlarm.isSelected() && songName.equals(""))
        {
            ObligationNotificationLabel.setText("Please enter a song name");
            return;
        }
        
        String duration = Integer.toString((3600*(Integer)ObligationHSpinner.getValue())+ (60*(Integer)ObligationMSpinner.getValue()) + (Integer)ObligationSSpinner.getValue());
        int status = 0;
        try
        {
            status = Request.Post("http://localhost:8080/MavenKorisnickiServis/REST/Obligation/updateObligation")
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .bodyForm(Form.form()
                    .add("date", dateS)
                    .add("obligationName", name)
                    .add("duration", duration)
                    .add("hasAddress", Boolean.toString(ObligationHasDestination.isSelected()))
                    .add("hasAlarm", Boolean.toString(ObligationAlarm.isSelected()))
                    .add("country", country)
                    .add("city", city)
                    .add("zip", zip)
                    .add("street", street)
                    .add("songName", songName)
                    .add("obligationID", Integer.toString(Obligations.obligations.get(index).getId()))
                    .build()).execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                setEvrythingUp();
                ObligationNotificationLabel.setText("Obligation successfully updated");
                ObligationNotificationLabel.setForeground(new Color(0, 200, 0));
                ObligationList.setSelectedIndex(-1);
            }
            else if(status == 403)
                ObligationNotificationLabel.setText("Can't fit");
            else
                ObligationNotificationLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            ObligationNotificationLabel.setText("Something went wrong while trying to update an obligation.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_ObligationUpdateButtonActionPerformed

    private void ObligationDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ObligationDeleteButtonActionPerformed
        ObligationNotificationLabel.setForeground(new Color(255,0,0));
        int index = ObligationList.getSelectedIndex();
        if(index < 0)
        {
            ObligationNotificationLabel.setText("Please select an obligation");
            return;
        }
        int status = 0;
        try
        {
            status = Request.Delete("http://localhost:8080/MavenKorisnickiServis/REST/Obligation/"+Integer.toString(Obligations.obligations.get(index).getId()))
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                setEvrythingUp();
                ObligationNotificationLabel.setText("Obligation successfully deleted");
                ObligationNotificationLabel.setForeground(new Color(0, 200, 0));
                ObligationList.setSelectedIndex(-1);
            }
            else
                ObligationNotificationLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            ObligationNotificationLabel.setText("Something went wrong while trying to delete an obligation.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_ObligationDeleteButtonActionPerformed

    private void AlarmLogOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlarmLogOutButtonActionPerformed
        logOut();
    }//GEN-LAST:event_AlarmLogOutButtonActionPerformed

    private void AlarmCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlarmCreateButtonActionPerformed
        AlarmNotificationLabel.setForeground(new Color(255, 0, 0));
        String dateS = AlarmDateTextBox.getText();
        String songName = AlarmSongNameTextBox.getText();
        if(dateS.equals("") || songName.equals(""))
        {
            AlarmNotificationLabel.setText("All text boxes are mandatory");
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try{
        Date date = formatter.parse(dateS);
        }catch(Exception e)
        {
            AlarmNotificationLabel.setText("Date format is incorrect");
            return;
        }
        int status = 0;
        try
        {
            status = Request.Post("http://localhost:8080/MavenKorisnickiServis/REST/Alarm/createAlarm")
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .bodyForm(Form.form()
                    .add("date", dateS)
                    .add("songName", songName)
                    .add("repeat", AlarmRepeatCheckBox.isSelected()?Integer.toString(1):Integer.toString(0))
                    .build()).execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                setEvrythingUp();
                AlarmNotificationLabel.setText("Alarm successfully created");
                AlarmNotificationLabel.setForeground(new Color(0, 200, 0));
                AlarmsList.setSelectedIndex(-1);
            }
            else
                AlarmNotificationLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            AlarmNotificationLabel.setText("Something went wrong when trying to create an alarm.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_AlarmCreateButtonActionPerformed

    private void AlarmUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlarmUpdateButtonActionPerformed
        AlarmNotificationLabel.setForeground(new Color(255, 0, 0));
        int index = AlarmsList.getSelectedIndex();
        if(index < 0)
        {
            AlarmNotificationLabel.setText("Please select an alarm");
            return;
        }
        String dateS = AlarmDateTextBox.getText();
        String songName = AlarmSongNameTextBox.getText();
        if(dateS.equals("") || songName.equals(""))
        {
            AlarmNotificationLabel.setText("All text boxes are mandatory");
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try{
        Date date = formatter.parse(dateS);
        }catch(Exception e)
        {
            AlarmNotificationLabel.setText("Date format is incorrect");
            return;
        }
        int status = 0;
        try
        {
            status = Request.Post("http://localhost:8080/MavenKorisnickiServis/REST/Alarm/updateAlarm")
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .bodyForm(Form.form()
                    .add("date", dateS)
                    .add("songName", songName)
                    .add("repeat", AlarmRepeatCheckBox.isSelected()?Integer.toString(1):Integer.toString(0))
                    .add("alarmID", Integer.toString(Alarms.alarms.get(index).getId()))
                    .build()).execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                setEvrythingUp();
                AlarmNotificationLabel.setText("Alarm successfully updated");
                AlarmNotificationLabel.setForeground(new Color(0, 200, 0));
                AlarmsList.setSelectedIndex(-1);
            }
            else
                AlarmNotificationLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            AlarmNotificationLabel.setText("Something went wrong when trying to update an alarm.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_AlarmUpdateButtonActionPerformed

    private void AlarmDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlarmDeleteButtonActionPerformed
        AlarmNotificationLabel.setForeground(new Color(255, 0, 0));
        int index = AlarmsList.getSelectedIndex();
        if(index < 0)
        {
            AlarmNotificationLabel.setText("Please select an alarm");
            return;
        }
        int status = 0;
        try
        {
            status = Request.Delete("http://localhost:8080/MavenKorisnickiServis/REST/Alarm/"+Integer.toString(Alarms.alarms.get(index).getId()))
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                setEvrythingUp();
                AlarmNotificationLabel.setText("Alarm successfully deleted");
                AlarmNotificationLabel.setForeground(new Color(0, 200, 0));
                AlarmsList.setSelectedIndex(-1);
            }
            else
                AlarmNotificationLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            AlarmNotificationLabel.setText("Something went wrong when trying to delete an alarm.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_AlarmDeleteButtonActionPerformed

    private void MusicGetAllPlayedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MusicGetAllPlayedActionPerformed
        JDialog dialog = new JDialog(MainFrame);
        dialog.setPreferredSize(new Dimension(300,500));
        dialog.setSize(dialog.getPreferredSize());
        dialog.setLocationRelativeTo(null);
        String text = "";
        try{
            HttpEntity r_entity = Request.Get("http://localhost:8080/MavenKorisnickiServis/REST/User/playedMusic")
                        .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                        .execute().returnResponse().getEntity();
            text = EntityUtils.toString(r_entity);
            JLabel tmp = new JLabel(text, SwingConstants.CENTER);
            tmp.setText("<html>"+text.replace(System.lineSeparator(), "<br/>")+"</html>");
            dialog.add(tmp);
            dialog.setVisible(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_MusicGetAllPlayedActionPerformed

    private void MusicPlayButtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MusicPlayButtoActionPerformed
        MusicNotificationLabel.setForeground(new Color(255, 0, 0));
        String songName = MusicSongNameTextBox.getText();
        if(songName.equals(""))
        {
            MusicNotificationLabel.setText("Song name is requiered.");
            return;
        }
        try
        {
            int status = Request.Post("http://localhost:8080/MavenKorisnickiServis/REST/User/playMusic")
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .bodyForm(Form.form()
                    .add("songName", songName)
                    .build()).execute().returnResponse().getStatusLine().getStatusCode();
            if(status == 200)
            {
                MusicNotificationLabel.setText("Music successfully played");
                MusicNotificationLabel.setForeground(new Color(0, 200, 0));
            }
            else
                MusicNotificationLabel.setText("Error "+status);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            MusicNotificationLabel.setText("Something went wrong while trying to play the song");
        }
    }//GEN-LAST:event_MusicPlayButtoActionPerformed

    private void CalculatorLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CalculatorLogOutActionPerformed
        logOut();
    }//GEN-LAST:event_CalculatorLogOutActionPerformed

    private void CalculatorCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CalculatorCheckBoxItemStateChanged
        CalculatorSCountryTextBox.setEnabled(!CalculatorCheckBox.isSelected());
        CalculatorSCityTextBox.setEnabled(!CalculatorCheckBox.isSelected());
        CalculatorSZipTextBox.setEnabled(!CalculatorCheckBox.isSelected());
        CalculatorSStreetTextBox.setEnabled(!CalculatorCheckBox.isSelected());
    }//GEN-LAST:event_CalculatorCheckBoxItemStateChanged

    private void CalculatorPostButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CalculatorPostButtonActionPerformed
        CalculatorLabel.setForeground(new Color(0, 0, 0));
        String country = CalculatorDCountryTextBox.getText();
        String city = CalculatorDCityTextBox.getText();
        String zip = CalculatorDZipTextBox.getText();
        String street = CalculatorDStreetTextBox.getText();
        if(country.equals("") || city.equals("") || zip.equals("") || street.equals(""))
        {
            CalculatorLabel.setForeground(new Color(255, 0, 0));
            CalculatorLabel.setText("Please fill in the destination text boxes");
            return;
        }
        String destination = street + "," + zip + " " + city + "," + country;
        country = CalculatorSCountryTextBox.getText();
        city = CalculatorSCityTextBox.getText();
        zip = CalculatorSZipTextBox.getText();
        street = CalculatorSStreetTextBox.getText();
        if(!CalculatorCheckBox.isSelected() && (country.equals("") || city.equals("") || zip.equals("") || street.equals("")))
        {
            CalculatorLabel.setForeground(new Color(255, 0, 0));
            CalculatorLabel.setText("Please fill in the starting location text boxes");
            return;
        }
        String start = street + "," + zip + " " + city + "," + country;
        try
        {
            HttpResponse res = Request.Post("http://localhost:8080/MavenKorisnickiServis/REST/Obligation/calculator")
                    .addHeader(HttpHeaders.AUTHORIZATION, credentials(Username, Password))
                    .bodyForm(Form.form()
                    .add("locationA", start)
                    .add("locationB", destination)
                    .add("UseCurrLocation", Boolean.toString(CalculatorCheckBox.isSelected()))
                    .build()).execute().returnResponse();
            int status = res.getStatusLine().getStatusCode();
            if(status == 200)
            {
                HttpEntity r_entity = res.getEntity();
                String answer = EntityUtils.toString(r_entity);
                CalculatorLabel.setText("Trip duration:"+answer);
            }
            else
            {
                CalculatorLabel.setForeground(new Color(255, 0, 0));
                CalculatorLabel.setText("Error:"+status);
            }
        }
        catch(Exception e)
        {
            CalculatorLabel.setForeground(new Color(255, 0, 0));
            CalculatorLabel.setText("Something went wrong when trying to create an obligation.");
            e.printStackTrace();
            return;
        }
    }//GEN-LAST:event_CalculatorPostButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KorisnickiUredjaj.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KorisnickiUredjaj.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KorisnickiUredjaj.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KorisnickiUredjaj.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KorisnickiUredjaj().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AlarmCreateButton;
    private javax.swing.JTextField AlarmDateTextBox;
    private javax.swing.JButton AlarmDeleteButton;
    private javax.swing.JButton AlarmLogOutButton;
    private javax.swing.JLabel AlarmNotificationLabel;
    private javax.swing.JPanel AlarmPanel;
    private javax.swing.JCheckBox AlarmRepeatCheckBox;
    private javax.swing.JTextField AlarmSongNameTextBox;
    private javax.swing.JButton AlarmUpdateButton;
    private javax.swing.JList<String> AlarmsList;
    private javax.swing.JCheckBox CalculatorCheckBox;
    private javax.swing.JTextField CalculatorDCityTextBox;
    private javax.swing.JTextField CalculatorDCountryTextBox;
    private javax.swing.JTextField CalculatorDStreetTextBox;
    private javax.swing.JTextField CalculatorDZipTextBox;
    private javax.swing.JLabel CalculatorLabel;
    private javax.swing.JButton CalculatorLogOut;
    private javax.swing.JPanel CalculatorPanel;
    private javax.swing.JButton CalculatorPostButton;
    private javax.swing.JTextField CalculatorSCityTextBox;
    private javax.swing.JTextField CalculatorSCountryTextBox;
    private javax.swing.JTextField CalculatorSStreetTextBox;
    private javax.swing.JTextField CalculatorSZipTextBox;
    private javax.swing.JLabel FailedToLogInLabel;
    private javax.swing.JButton LogInButton;
    private javax.swing.JPanel LogInPanel;
    private javax.swing.JButton MusicGetAllPlayed;
    private javax.swing.JLabel MusicNotificationLabel;
    private javax.swing.JPanel MusicPanel;
    private javax.swing.JButton MusicPlayButto;
    private javax.swing.JTextField MusicSongNameTextBox;
    private javax.swing.JCheckBox ObligationAlarm;
    private javax.swing.JTextField ObligationCityTextBox;
    private javax.swing.JTextField ObligationCountryTextBox;
    private javax.swing.JButton ObligationCreateButton;
    private javax.swing.JTextField ObligationDateTextBox;
    private javax.swing.JButton ObligationDeleteButton;
    private javax.swing.JSpinner ObligationHSpinner;
    private javax.swing.JCheckBox ObligationHasDestination;
    private javax.swing.JList<String> ObligationList;
    private javax.swing.JButton ObligationLogOutButton;
    private javax.swing.JSpinner ObligationMSpinner;
    private javax.swing.JTextField ObligationNameTextBox;
    private javax.swing.JLabel ObligationNotificationLabel;
    private javax.swing.JPanel ObligationPanel;
    private javax.swing.JSpinner ObligationSSpinner;
    private javax.swing.JTextField ObligationSongNameTextBox;
    private javax.swing.JTextField ObligationStreetTextBox;
    private javax.swing.JButton ObligationUpdateButton;
    private javax.swing.JTextField ObligationZipTextBox;
    private javax.swing.JTextField PasswordLogInTextBox;
    private javax.swing.JButton RegisterButton;
    private javax.swing.JButton RegisterCancelButton;
    private javax.swing.JTextField RegisterCityTextBox;
    private javax.swing.JTextField RegisterCountryTextBox;
    private javax.swing.JButton RegisterCreateButton;
    private javax.swing.JLabel RegisterFailedLabel;
    private javax.swing.JTextField RegisterNameTextBox;
    private javax.swing.JPanel RegisterPanel;
    private javax.swing.JTextField RegisterPasswordTextBox;
    private javax.swing.JTextField RegisterStreetTextBox;
    private javax.swing.JTextField RegisterSurnameTextBox;
    private javax.swing.JTextField RegisterUsernameTextBox;
    private javax.swing.JTextField RegisterZipTextBox;
    private javax.swing.JPanel RegistrationButtonPanel;
    private javax.swing.JTextField UserNameLogInTextBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
