package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;



@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

    Dao dao = new Dao();
    private Boolean isAdmin = false;
    private String user = "";

    // Menu items
    JMenuItem mnuItemExit; // Exit menu item
    JMenuItem mnuItemUpdate; // Update Ticket menu item
    JMenuItem mnuItemDelete; // Delete Ticket menu item
    JMenuItem mnuItemOpenTicket; // Open Ticket menu item
    JMenuItem mnuItemViewTicket; // View Ticket menu item
    JMenuItem mnuItemCloseTicket; // Close Ticket menu item

    /**
     * Constructor for Tickets class
     * @param isAdmin Indicates if the user is an admin
     * @param user The username of the user
     */
    public Tickets(Boolean isAdmin, String user) {
        this.isAdmin = isAdmin;
        this.user = user;
        createMenu();
        prepareGUI();
    }

    /**
     * Create menu items
     */
    private void createMenu() {
        // Create menu bar
        JMenuBar bar = new JMenuBar();
        JMenu mnuFile = new JMenu("File");
        JMenu mnuAdmin = new JMenu("Admin");
        JMenu mnuTickets = new JMenu("Tickets");

        // Create File menu items
        mnuItemExit = new JMenuItem("Exit");
        mnuFile.add(mnuItemExit);

        // Create Admin menu items if the user is an admin
        if (isAdmin) {
            mnuItemUpdate = new JMenuItem("Update Ticket");
            mnuAdmin.add(mnuItemUpdate);

            mnuItemDelete = new JMenuItem("Delete Ticket");
            mnuAdmin.add(mnuItemDelete);

            // Adding close ticket option for admin
            mnuItemCloseTicket = new JMenuItem("Close Ticket");
            mnuAdmin.add(mnuItemCloseTicket);
        }

        // Create Tickets menu items
        mnuItemOpenTicket = new JMenuItem("Open Ticket");
        mnuTickets.add(mnuItemOpenTicket);

        mnuItemViewTicket = new JMenuItem("View Ticket");
        mnuTickets.add(mnuItemViewTicket);

        // Add menu items to the menu bar
        bar.add(mnuFile);
        if (isAdmin) {
            bar.add(mnuAdmin);
        }
        bar.add(mnuTickets);

        setJMenuBar(bar);

        // Add action listeners for menu items
        mnuItemExit.addActionListener(this);
        if (isAdmin) {
            mnuItemUpdate.addActionListener(this);
            mnuItemDelete.addActionListener(this);
            // Adding action listener for closing ticket
            mnuItemCloseTicket.addActionListener(this);
        }
        mnuItemOpenTicket.addActionListener(this);
        mnuItemViewTicket.addActionListener(this);
    }

    /**
     * Prepare GUI settings
     */
    private void prepareGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mnuItemExit) {
            System.exit(0);
        } else if (e.getSource() == mnuItemOpenTicket) {
            String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");
            int id = dao.insertTicket(user, ticketDesc);
            if (id != 0) {
                System.out.println("Ticket ID : " + id + " created successfully!!!");
                JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
            } else {
                System.out.println("Ticket cannot be created!!!");
            }
        } else if (e.getSource() == mnuItemViewTicket) {
            try {
                ResultSet resultSet;
                if (isAdmin) {
                    resultSet = dao.readTickets();
                } else {
                    resultSet = dao.readUserTickets(user);
                }
                JTable jt = new JTable(ticketsJTable.buildTableModel(resultSet));
                jt.setBounds(30, 40, 200, 400);
                JScrollPane sp = new JScrollPane(jt);
                add(sp);
                setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if (isAdmin && e.getSource() == mnuItemCloseTicket) {
            // Implement closing ticket functionality for admin
            String ticketId = JOptionPane.showInputDialog(null, "Enter ticket ID to close:");
            if (ticketId != null) {
                dao.closeTicket(Integer.parseInt(ticketId)); // Close the ticket
                JOptionPane.showMessageDialog(null, "Ticket closed successfully!");
                System.out.println("Ticket ID : " + ticketId + " closed successfully!!!");// Display success message
            }
        } else if (isAdmin && e.getSource() == mnuItemUpdate) {
            // Implement update ticket functionality for admin
            String ticketId = JOptionPane.showInputDialog(null, "Enter ticket ID to update:");
            String newDescription = JOptionPane.showInputDialog(null, "Enter new description:");
            if (ticketId != null && newDescription != null) {
                dao.updateTicket(Integer.parseInt(ticketId), newDescription); // Update the ticket
                JOptionPane.showMessageDialog(null, "Ticket updated successfully!"); // Display success message
            }
        } else if (isAdmin && e.getSource() == mnuItemDelete) {
            // Implement delete ticket functionality for admin
            String ticketId = JOptionPane.showInputDialog(null, "Enter ticket ID to delete:");
            if (ticketId != null) {
                dao.deleteTicket(Integer.parseInt(ticketId)); // Delete the ticket
                JOptionPane.showMessageDialog(null, "Ticket deleted successfully!"); // Display success message
            }
        }
    }
}
