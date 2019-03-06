import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private String[][] data;
    private EventManager manager = new EventManager();
    private double firstYear;
    private int numberOfYears;
    private double yearToPredict;

    ventana() {
        super("Temperatura por año");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        //container
        cp = getContentPane();
        cp.setLayout(new GridLayout(2, 1));
        cp.add(initPanel, BorderLayout.CENTER);
    }

    class EventManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == printB) {
                try {
                    firstYear = Double.parseDouble(initYearTF.getText().substring(0, 4));
                    numberOfYears = Integer.parseInt(noYearsTF.getText().substring(0, 1));
                    yearToPredict = Double.parseDouble(yearToPredictTF.getText().substring(0, 4));

                    if ((firstYear < 1890) || (firstYear > 2000)) {
                        throw new StringIndexOutOfBoundsException("\n\nAños válidos: 1890-2000.");
                    }
                    if (numberOfYears > (((2010 - firstYear) / 10) + 1)) {
                        throw new StringIndexOutOfBoundsException("\n\n# de años no pueden pasar del 2010, a partir de la fecha inicial.");
                    }
                    if (numberOfYears < 2) {
                        throw new StringIndexOutOfBoundsException("\n\n# de años debe ser mayor a 1.");
                    }
                    if (yearToPredict < 2010) {
                        throw new StringIndexOutOfBoundsException("\n\nAño a predicar debe ser mayor al 2010");
                    }
                    double temp = firstYear;
                    data = new String[numberOfYears][2];
                    for (int i = 0; i < numberOfYears; i++) {
                        data[i][0] = String.valueOf(temp);
                        data[i][1] = String.valueOf(i + 1);
                        temp += 10;
                    }

                    //table model
                    table = new JTable(data, columnNames);
                    table.setBounds(260, 0, 100, 100);
                    //scrollpane = new JScrollPane(table);


                    //position table panel
                    tablePanel = new JPanel();
                    tablePanel.setLayout(null);
                    calculateB = new JButton("Calcular");
                    calculateB.setBounds(260, 150, 80, 25);
                    calculateB.addActionListener(manager);
                    //tablePanel.setLayout(null);

                    //table = new JTable(data, columnNames);
                    tablePanel.add(table);
                    tablePanel.add(calculateB);
                    cp.add(tablePanel, BorderLayout.CENTER);

                    //refresh view
                    tablePanel.updateUI();
                    tablePanel.repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ventana.this, e, "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (event.getSource() == calculateB) {
                double temp = firstYear;

                linearRegression ec = new linearRegression();
                double[] arrayX = new double[numberOfYears];
                double[] arrayY = new double[numberOfYears];
                int i = 0;
                do {
                    arrayX[i] = temp;
                    arrayY[i] = (double) table.getSelectedColumn();
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
                System.out.println("\n\n-------------------\nAño   |   ºC\n-------------------\n" + String.format("%.0f", yearToPredict) + "  |   " + String.format("%.2f", formula) + "\n-------------------");


            }

        }
    }
}
