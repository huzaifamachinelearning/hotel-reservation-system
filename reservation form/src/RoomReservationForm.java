import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class RoomReservationForm extends JFrame {
    Connection c;

    public RoomReservationForm( Connection c) {
        this.c=c;

        setTitle("Room Reservation System");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create panel with GridLayout
        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Customer details
        panel.add(new JLabel("Customer ID:"));
        JTextField customerIdField = new JTextField();
        panel.add(customerIdField);

        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Contact No:"));
        JTextField contactField = new JTextField();
        panel.add(contactField);

        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);

        // Room details
        // Room Type with Search button in a horizontal panel
        panel.add(new JLabel("Room Type:"));
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{"economy", "standard", "deluxe"});
        panel.add(roomTypeCombo);

        //Functionality






        panel.add(new JLabel("Check-In Date:"));
        JTextField checkInField = new JTextField("DD-MON-YYYY");
        panel.add(checkInField);

        panel.add(new JLabel("Check-Out Date:"));
        JTextField checkOutField = new JTextField("DD-MON-YYYY");
        panel.add(checkOutField);
        panel.add(new JLabel("Available Room No:"));
        JPanel availableRoomPanel = new JPanel(new BorderLayout(5, 0)); // 5 px gap
        JComboBox<String> roomNoCombo = new JComboBox<>();
        availableRoomPanel.add(roomNoCombo, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        availableRoomPanel.add(searchButton, BorderLayout.EAST);

        panel.add(availableRoomPanel);

// Search Button Functionality
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedRoomType = (String) roomTypeCombo.getSelectedItem();
                    String checkInDate = checkInField.getText().trim();
                    String checkOutDate = checkOutField.getText().trim();

                    if (checkInDate.isEmpty() || checkOutDate.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter both Check-In and Check-Out dates.");
                        return; // Stop execution
                    }


                    String sql =
                            "SELECT DISTINCT(r.room_id) " +
                                    "FROM room r, reserv_room rr " +
                                    "WHERE r.room_id = rr.room_id(+) " +
                                    "AND ( " +
                                    "  (rr.reserv_id IS NULL AND room_type_id = ( " +
                                    "    SELECT room_type_id FROM room_type WHERE type_name = ?)) " +
                                    "  OR (r.room_id IN ( " +
                                    "    SELECT room_id FROM reserv_room WHERE reserv_id IN ( " +
                                    "      SELECT reserv_id FROM reserv WHERE ( " +
                                    "        TO_DATE(?,'DD-MON-YYYY') < TRUNC(check_in_date) OR TO_DATE(?,'DD-MON-YYYY') > TRUNC(check_out_date)) " +
                                    "        AND reserv_status IN ('booked', 'checked_in'))) " +
                                    "    AND room_type_id = (SELECT room_type_id FROM room_type WHERE type_name = ?)) " +
                                    "  OR (r.room_id NOT IN ( " +
                                    "    SELECT NVL(room_id, -1) FROM reserv_room WHERE reserv_id IN ( " +
                                    "      SELECT reserv_id FROM reserv WHERE reserv_status IN ('booked', 'checked-in'))) " +
                                    "    AND r.room_id IN ( " +
                                    "      SELECT room_id FROM reserv_room WHERE reserv_id IN ( " +
                                    "        SELECT reserv_id FROM reserv WHERE reserv_status IN ('completed', 'cancelled'))) " +
                                    "    AND room_type_id = (SELECT room_type_id FROM room_type WHERE type_name = ?)) " +
                                    ")";



                    PreparedStatement ps = c.prepareStatement(sql);
                    ps.setString(1, selectedRoomType);  // For first room_type
                    ps.setString(2, checkOutDate); // check-in boundary
                    ps.setString(3, checkInDate); // check-out boundary
                    ps.setString(4, selectedRoomType);  // For second room_type
                    ps.setString(5, selectedRoomType);


                    ResultSet rs = ps.executeQuery();
                    roomNoCombo.removeAllItems();
                    boolean roomFound = false;

                    while (rs.next()) {

                        int roomId = rs.getInt("room_id");
                        System.out.println(roomId);
                        roomNoCombo.addItem(String.valueOf(roomId));
                        roomFound = true;
                    }
                    roomNoCombo.revalidate();
                    roomNoCombo.repaint();

                    if (!roomFound) {
                        JOptionPane.showMessageDialog(null, "No available rooms found.");
                    }

                    rs.close();
                    ps.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error fetching available rooms.");
                }
            }
        });


        panel.add(new JLabel("Advance Payment:"));
        JTextField paymentField = new JTextField();
        panel.add(paymentField);

        panel.add(new JLabel("Checked In:"));
        JCheckBox checkedInBox = new JCheckBox();
        panel.add(checkedInBox);

        // Submit Button
        JButton submitBtn = new JButton("Create Reservation");
        //FUCNTIONALITY
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guestIdText     = customerIdField.getText().trim();
                String guestName       = nameField.getText().trim();
                String guestContact    = contactField.getText().trim();
                String guestEmail      = emailField.getText().trim();
                String roomIdText      = (String)roomNoCombo.getSelectedItem();
                String advanceText     = paymentField.getText().trim();
                String checkInText     = checkInField.getText().trim();
                String checkOutText    = checkOutField.getText().trim();
                boolean  checkedIn     = checkedInBox.isSelected();

                // Simple validation
                if (guestIdText.isEmpty() || guestName.isEmpty()
                        || roomIdText == null || checkInText.isEmpty() || checkOutText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all required fields.");
                    return;
                }

                Connection conn = c;
                try {
                    conn.setAutoCommit(false);

                    // 1) Does guest exist?
                    PreparedStatement psCheck = conn.prepareStatement(
                            "SELECT guest_id FROM guest WHERE guest_id = ?");
                    psCheck.setInt(1, Integer.parseInt(guestIdText));
                    ResultSet rsCheck = psCheck.executeQuery();

                    if (rsCheck.next()) {
                        // 2a) Guest exists → UPDATE
                        PreparedStatement psUpd = conn.prepareStatement(
                                "UPDATE guest " +
                                        "   SET name = ?, contact_no = ?, email = ? " +
                                        " WHERE guest_id = ?");
                        psUpd.setString(1, guestName);
                        psUpd.setString(2, guestContact);
                        psUpd.setString(3, guestEmail);
                        psUpd.setInt   (4, Integer.parseInt(guestIdText));
                        psUpd.executeUpdate();
                        psUpd.close();
                    } else {
                        // 2b) Guest does not exist → INSERT
                        PreparedStatement psIns = conn.prepareStatement(
                                "INSERT INTO guest(guest_id,name,contact_no,email) " +
                                        "VALUES (?, ?, ?, ?)");
                        psIns.setInt   (1, Integer.parseInt(guestIdText));
                        psIns.setString(2, guestName);
                        psIns.setString(3, guestContact);
                        psIns.setString(4, guestEmail);
                        psIns.executeUpdate();
                        psIns.close();
                    }
                    rsCheck.close();
                    psCheck.close();

                    // 3) Create reservation
                    //    Note: recepId is hardcoded as 1 here; adjust as needed
                    PreparedStatement psRes = conn.prepareStatement(
                            "INSERT INTO reserv(reserv_id,  guest_id, recep_id, advance_payment, reserv_time, check_in_date, check_out_date, reserv_status) " +
                                    "SELECT NVL(MAX(reserv_id), 0) + 1,?, 1, ?, TRUNC(SYSDATE), TO_DATE(?, 'DD-MON-YYYY'), TO_DATE(?, 'DD-MON-YYYY'), ? " +
                                    "  FROM reserv");

                    psRes.setInt   (1, Integer.parseInt(guestIdText));
                    psRes.setBigDecimal(2, new java.math.BigDecimal(advanceText.isEmpty() ? "0" : advanceText));
                    psRes.setString(3, checkInText);
                    psRes.setString(4, checkOutText);
                    psRes.setString(5, checkedIn ? "checked-in" : "booked");
                    psRes.executeUpdate();

                    psRes.close();
                    PreparedStatement psRR = conn.prepareStatement(
                            "INSERT INTO reserv_room(reserv_id,room_id) SELECT MAX(reserv_id), ?  FROM reserv ");

                    psRR.setInt(1, Integer.parseInt(roomIdText));
                    psRR.executeUpdate();
                    psRR.close();

                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Reservation created successfully.");

                } catch (Exception ex) {
                    try { conn.rollback(); } catch (SQLException rbe) { /* log */ }
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to create reservation: " + ex.getMessage());
                } finally {
                    try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
                }
            }
        });

        panel.add(submitBtn);

        // Empty label to fill grid
        panel.add(new JLabel(""));

        // Add panel to frame
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        Connection con=null;
        try {
            Class.forName( "oracle.jdbc.driver.OracleDriver");
            con= DriverManager.getConnection(  "jdbc:oracle:thin:@localhost:1521:orcl",  "HUZAIFA","tiger");
            if (con!=null) {
                System.out.println("Successful");
                new RoomReservationForm(con);
            }
            else
                System.out.println("Error");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }



    }
}
