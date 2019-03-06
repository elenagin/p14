import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings({"ALL", "CanBeFinal"})
public class ventana extends JFrame {
    private JButton printB, calculateB;
    @SuppressWarnings("FieldCanBeLocal")
    private JPanel initPanel, tablePanel;
    private JScrollPane scrollpane;
    private JTable table;
    @SuppressWarnings("FieldCanBeLocal")
    private JLabel title, initYearL, noYearsL, yearToPredictL;
    private JTextField initYearTF, noYearsTF, yearToPredictTF;
    private Container cp;
    private String[] columnNames = new String[]{"Año", "Temperatura"};
    @SuppressWarnings("FieldCanBeLocal")
    private String[][] data;
    private EventManager manager = new EventManager();
    private double firstYear;
    private int numberOfYears, panelCount = 0;
    private double yearToPredict;

    ventana() {
        super("Temperatura por año");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        //position init panel
        initPanel = new JPanel();
        initPanel.setLayout(null);
        title = new JLabel("Ingresa los datos correspondientes: ");
        title.setBounds(20, 20, 250, 25);
        initYearL = new JLabel("Año inicial");
        initYearL.setBounds(20, 50, 80, 25);
        initYearTF = new JTextField();
        initYearTF.setBounds(100, 50, 50, 25);
        noYearsL = new JLabel("# años a ingresar");
        noYearsL.setBounds(180, 50, 150, 25);
        noYearsTF = new JTextField();
        noYearsTF.setBounds(300, 50, 50, 25);
        yearToPredictL = new JLabel("Año a predicar");
        yearToPredictL.setBounds(400, 50, 150, 25);
        yearToPredictTF = new JTextField();
        yearToPredictTF.setBounds(500, 50, 50, 25);
        printB = new JButton("Ingresar temperaturas");
        printB.setBounds(210, 100, 180, 25);
        printB.addActionListener(manager);

        //add to initial panel
        initPanel.add(title);
        initPanel.add(initYearL);
        initPanel.add(noYearsL);
        initPanel.add(yearToPredictL);
        initPanel.add(printB);
        initPanel.add(initYearTF);
        initPanel.add(noYearsTF);
        initPanel.add(yearToPredictTF);
        tablePanel = new JPanel();
        
        //container
        cp = getContentPane();
        cp.setLayout(new GridLayout(2, 1));
        cp.add(initPanel, BorderLayout.CENTER);
        cp.add(tablePanel, BorderLayout.CENTER);
    }

    class EventManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if ((event.getSource() == printB) && (panelCount == 0)) {
                try {
                    panelCount = 1;
                    tablePanel.removeAll();
                    firstYear = Double.parseDouble(initYearTF.getText().substring(0, 4));
                    numberOfYears = Integer.parseInt(noYearsTF.getText());
                    yearToPredict = Double.parseDouble(yearToPredictTF.getText().substring(0, 4));

                    if ((firstYear < 1890) || (firstYear > 2000)) {
                        initYearTF.setBackground(Color.red);
                        throw new StringIndexOutOfBoundsException("\n\nAños válidos: 1890-2000.");
                    }
                    if (numberOfYears > (((2010 - firstYear) / 10) + 1)) {
                        noYearsTF.setBackground(Color.red);
                        throw new StringIndexOutOfBoundsException("\n\n# de años no pueden pasar del 2010, a partir de la fecha inicial.");
                    }
                    if (numberOfYears < 2) {
                        noYearsTF.setBackground(Color.red);
                        throw new StringIndexOutOfBoundsException("\n\n# de años debe ser mayor a 1.");
                    }
                    if (yearToPredict < 2010) {
                        yearToPredictTF.setBackground(Color.red);
                        throw new StringIndexOutOfBoundsException("\n\nAño a predicar debe ser mayor al 2010");
                    }
                    initYearTF.setBackground(Color.white);
                    noYearsTF.setBackground(Color.white);
                    yearToPredictTF.setBackground(Color.white);
                    double temp = firstYear;
                    data = new String[numberOfYears][2];
                    for (int i = 0; i < numberOfYears; i++) {
                        data[i][0] = String.valueOf(temp);
                        data[i][1] = String.valueOf(" ");
                        temp += 10;
                    }

                    //table model
                    table = new JTable(data, columnNames);
                    scrollpane = new JScrollPane(table);
                    scrollpane.setBounds(175, 0, 250, 100);
                    calculateB = new JButton("Calcular");
                    calculateB.setBounds(260, 150, 80, 25);
                    calculateB.addActionListener(manager);

                    //position table panel
                    //cp.remove(tablePanel);
                    //tablePanel = new JPanel();
                    tablePanel.setLayout(null);
                    tablePanel.add(scrollpane);
                    tablePanel.add(calculateB);

                    //container
                    //cp.add(tablePanel, BorderLayout.CENTER);

                    //refresh view
                    tablePanel.updateUI();
                    tablePanel.repaint();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ventana.this, e, "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                //cp.remove(tablePanel);
                panelCount = 0;
                //actionPerformed(event);
            }

            if ((event.getSource() == calculateB) && (panelCount == 0)) {
                try {
                    linearRegression ec = new linearRegression();
                    double temp = firstYear;
                    double[] arrayX = new double[numberOfYears];
                    double[] arrayY = new double[numberOfYears];
                    int i = 0;
                    String value;
                    do {
                        arrayX[i] = temp;
                        value = table.getValueAt(i, 1).toString();
                        arrayY[i] = Double.valueOf(value);
                        temp += 10;
                        i++;
                    } while (temp <= (firstYear + (10 * (numberOfYears - 1))));

                    //implements linear regression method
                    ec.fsumX(arrayX);
                    ec.fsumXSquared(arrayX);
                    ec.fsumY(arrayY);
                    ec.fsumXY(arrayX, arrayY);
                    ec.setSlope();
                    ec.setOrigin();
                    double m = ec.getSlope();
                    double b = ec.getOrigin();
                    double formula = (m * yearToPredict) + b;
                    JOptionPane.showMessageDialog(ventana.this, "\n\n------------\nAño   |   ºC\n------------\n".concat(String.format("%.0f", yearToPredict)).concat("  |   ").concat(String.format("%.2f", formula)).concat("\n------------"), "Resultado", JOptionPane.PLAIN_MESSAGE);
                    //System.out.println("\n\n-------------------\nAño   |   ºC\n-------------------\n" + String.format("%.0f", yearToPredict) + "  |   " + String.format("%.2f", formula) + "\n-------------------");
                } catch (Exception e) {
                    scrollpane.setBackground(Color.red);
                    JOptionPane.showMessageDialog(ventana.this, e, "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
