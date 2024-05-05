package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("serial")
public class Login extends JFrame {

    Dao conn;

    public Login() {
        super("IT HELP DESK LOGIN");
        conn = new Dao();
        conn.createTables();
        setSize(400, 210);
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(null);

        JLabel lblUsername = new JLabel("Username", JLabel.LEFT);
        JLabel lblPassword = new JLabel("Password", JLabel.LEFT);
        JLabel lblStatus = new JLabel(" ", JLabel.CENTER);

        JTextField txtUname = new JTextField(10);
        JPasswordField txtPassword = new JPasswordField();
        JButton btn = new JButton("Submit");
        JButton btnExit = new JButton("Exit");

        lblStatus.setToolTipText("Contact help desk to unlock password");
        lblUsername.setHorizontalAlignment(JLabel.CENTER);
        lblPassword.setHorizontalAlignment(JLabel.CENTER);

        add(lblUsername);
        add(txtUname);
        add(lblPassword);
        add(txtPassword);
        add(btn);
        add(btnExit);
        add(lblStatus);

        btn.addActionListener(new ActionListener() {
            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean admin = false;
                count = count + 1;

                String query = "SELECT * FROM ptroj_users WHERE uname = ? AND upass = ?";
                try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
                    stmt.setString(1, txtUname.getText());
                    stmt.setString(2, new String(txtPassword.getPassword()));
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        admin = rs.getBoolean("admin");
                        new Tickets(admin, txtUname.getText());
                        setVisible(false);
                        dispose();
                    } else
                        lblStatus.setText("Try again! " + (3 - count) + " / 3 attempt(s) left");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnExit.addActionListener(e -> {
            conn.close();
            System.exit(0);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new Login();
    }
}
