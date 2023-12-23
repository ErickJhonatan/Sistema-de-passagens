package br.uece.flight;

import br.uece.flight.Flight;
import com.toedter.calendar.JDateChooser;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

public class EditFlightDialog extends JDialog {
    private Flight flight;
    private JTextField field1;
    private JTextField field2;
    private JDateChooser field3;
    private JDateChooser field4;
    private JFormattedTextField field5;
    private JFormattedTextField field6;
    private JButton okButton;
    private JButton cancelButton;

    public EditFlightDialog(JFrame parent, Flight flight) {
        super(parent, "Editar Voo", true);
        this.flight = flight;

        field1 = new JTextField();
        field2 = new JTextField();
        field3 = new JDateChooser();
        field3.getJCalendar().setMinSelectableDate(new Date());
        field4 = new JDateChooser();
        field4.setEnabled(false);
        NumberFormatter floatFormatter = new NumberFormatter(new DecimalFormat("#0.00"));
        floatFormatter.setValueClass(Float.class);
        floatFormatter.setMinimum(0f);
        floatFormatter.setMaximum(Float.MAX_VALUE);
        floatFormatter.setAllowsInvalid(false);
        floatFormatter.setCommitsOnValidEdit(true);
        field5 = new JFormattedTextField(floatFormatter);
        NumberFormatter intFormatter = new NumberFormatter(new DecimalFormat("#"));
        intFormatter.setValueClass(Integer.class);
        intFormatter.setMinimum(1);
        intFormatter.setMaximum(Integer.MAX_VALUE);
        intFormatter.setAllowsInvalid(false);
        intFormatter.setCommitsOnValidEdit(true);
        field6 = new JFormattedTextField(intFormatter);
        
        field1.setText(flight.getOriginCity());
        field2.setText(flight.getDestinationCity());
        field3.setDate(flight.getDepartureDate());
        field4.setDate(flight.getReturnDate());
        field5.setValue(flight.getPrice());
        field6.setValue(flight.getNumberOfSeats());

        okButton = new JButton("Editar");
        cancelButton = new JButton("Cancelar");

        JPanel panel = new JPanel();
        Integer margin = 10;
        Border emptyBorder = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
        Border etcherBorder = BorderFactory.createEtchedBorder();
        panel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etcherBorder));
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 2));
        panel.add(panel2);
        panel2.add(new JLabel("Cidade Origem*:"));
        panel2.add(field1);
        panel2.add(new JLabel("Cidade Destino*:"));
        panel2.add(field2);
        panel2.add(new JLabel("Data da Ida*:"));
        panel2.add(field3);
        panel2.add(new JLabel("Data da Volta:"));
        panel2.add(field4);
        panel2.add(new JLabel("Preço*:"));
        panel2.add(field5);
        panel2.add(new JLabel("Quantidade de Assentos*:"));
        panel2.add(field6);
        panel2.add(okButton);
        panel2.add(cancelButton);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
        
        field3.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() instanceof Date) {
                    Date selectedDate = (Date) evt.getNewValue();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(selectedDate);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    field4.setEnabled(true);
                    field4.getJCalendar().setMinSelectableDate(calendar.getTime());
                    field4.setDate(null);
                }
            }
        });

        okButton.addActionListener(e -> {
            
            String value1 = field1.getText();
            String value2 = field2.getText();
            Date value3 = field3.getDate();
            Date value4 = field4.getDate();
            Float value5 = (Float) field5.getValue();
            Integer value6 = (Integer) field6.getValue();
           
            if (value1.isBlank() || value2.isBlank() || value3 == null || value5 == null || value6 == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "Algum campo não foi preenchido!");
                return;
            }
            
            if(value1.equalsIgnoreCase(value2)){
                javax.swing.JOptionPane.showMessageDialog(null, "A cidade de destino não pode ser igual a de origem!");
                return;
            }
            
            Integer numberOfSeatsBought = flight.getNumberOfSeats() - flight.getNumberOfSeatsAvailable();
            
            if(value6 < numberOfSeatsBought){
                javax.swing.JOptionPane.showMessageDialog(null, "O número de assentos não pode ser menor que a quantidade de passagens já vendidas!");
                return;
            }
            
            Flight.update(flight.getId(), value1, value2, value3, value4, value5, value6, value6 - numberOfSeatsBought);

            javax.swing.JOptionPane.showMessageDialog(null, "Voo adicionado com sucesso!");

            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }
    public static void main(Flight flight) {
        EditFlightDialog dialog = new EditFlightDialog(new JFrame(), flight);
        dialog.setVisible(true);
    }
    
}