import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SortingVisualizers extends JPanel {
    private int[] array;
    private int delay = 50;
    private boolean isSorting = false;
    private boolean descendingOrder = true;
    private volatile boolean stopSorting = false; // Flag to stop sorting

    public SortingVisualizers(int size) {
        array = new int[size];
        generateNewArray(50, 400); // Default range between 50 and 400
    }

    public void generateNewArray(int min, int max) {
        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(max - min + 1) + min; // Generate numbers in the specified range
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background gradient
        Graphics2D g2d = (Graphics2D) g;
        Color gradientStart = new Color(30, 30, 30);
        Color gradientEnd = new Color(70, 70, 70);
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, getWidth(), getHeight(), gradientEnd));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw bars and values
        int width = getWidth();
        int height = getHeight();
        int barWidth = Math.max(1, width / array.length);

        g.setFont(new Font("Arial", Font.BOLD, Math.min(16, barWidth / 2))); // Dynamically resize font
        for (int i = 0; i < array.length; i++) {
            int value = array[i];
            int x = i * barWidth;
            int barHeight = value;

            g.setColor(Color.CYAN);
            g.fillRect(x, height - barHeight, barWidth - 2, barHeight);

            g.setColor(Color.WHITE);
            String valueStr = String.valueOf(value);
            int textWidth = g.getFontMetrics().stringWidth(valueStr);
            g.drawString(valueStr, x + (barWidth - textWidth) / 2, height - barHeight - 5);
        }
    }

    public void visualizeSorting(String algorithm) {
        new Thread(() -> {
            isSorting = true;
            stopSorting = false; // Reset the stop flag
            switch (algorithm) {
                case "Bubble Sort":
                    bubbleSort();
                    break;
                case "Selection Sort":
                    selectionSort();
                    break;
                case "Merge Sort":
                    mergeSort(0, array.length - 1);
                    break;
            }
            isSorting = false;
        }).start();
    }

    private void bubbleSort() {
        for (int i = 0; i < array.length - 1 && !stopSorting; i++) {
            for (int j = 0; j < array.length - i - 1 && !stopSorting; j++) {
                if ((descendingOrder && array[j] < array[j + 1]) || (!descendingOrder && array[j] > array[j + 1])) {
                    swap(j, j + 1);
                }
                repaintAndWait();
            }
        }
    }

    private void selectionSort() {
        for (int i = 0; i < array.length - 1 && !stopSorting; i++) {
            int extremeIndex = i;
            for (int j = i + 1; j < array.length && !stopSorting; j++) {
                if ((descendingOrder && array[j] > array[extremeIndex]) || (!descendingOrder && array[j] < array[extremeIndex])) {
                    extremeIndex = j;
                }
            }
            swap(i, extremeIndex);
            repaintAndWait();
        }
    }

    private void mergeSort(int left, int right) {
        if (left < right && !stopSorting) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right && !stopSorting) {
            if ((descendingOrder && array[i] >= array[j]) || (!descendingOrder && array[i] <= array[j])) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i <= mid && !stopSorting) {
            temp[k++] = array[i++];
        }

        while (j <= right && !stopSorting) {
            temp[k++] = array[j++];
        }

        for (i = left, k = 0; i <= right && !stopSorting; i++, k++) {
            array[i] = temp[k];
            repaintAndWait();
        }
    }

    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private void repaintAndWait() {
        repaint();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sorting Visualizer");
        SortingVisualizers panel = new SortingVisualizers(50);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(Color.DARK_GRAY);

        JLabel sizeLabel = new JLabel("Size: ");
        sizeLabel.setForeground(Color.WHITE);
        JSlider sizeSlider = new JSlider(10, 100, 50);
        sizeSlider.addChangeListener(e -> {
            panel.array = new int[sizeSlider.getValue()];
            panel.generateNewArray(50, 400);
        });

        JLabel speedLabel = new JLabel("Speed: ");
        speedLabel.setForeground(Color.WHITE);
        JSlider speedSlider = new JSlider(10, 200, 50);
        speedSlider.addChangeListener(e -> panel.delay = speedSlider.getValue());

        JCheckBox orderCheckBox = new JCheckBox("Descending Order", true);
        orderCheckBox.setBackground(Color.DARK_GRAY);
        orderCheckBox.setForeground(Color.WHITE);
        orderCheckBox.addActionListener(e -> panel.descendingOrder = orderCheckBox.isSelected());

        JButton newArrayButton = new JButton("New Array");
        newArrayButton.addActionListener(e -> {
            JTextField minField = new JTextField(5);
            JTextField maxField = new JTextField(5);

            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("Min:"));
            inputPanel.add(minField);
            inputPanel.add(Box.createHorizontalStrut(15));
            inputPanel.add(new JLabel("Max:"));
            inputPanel.add(maxField);

            int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Enter Array Range", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int min = Integer.parseInt(minField.getText().trim());
                    int max = Integer.parseInt(maxField.getText().trim());
                    panel.generateNewArray(min, max);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid integers.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton bubbleSortButton = new JButton("Bubble Sort");
        bubbleSortButton.addActionListener(e -> panel.visualizeSorting("Bubble Sort"));

        JButton selectionSortButton = new JButton("Selection Sort");
        selectionSortButton.addActionListener(e -> panel.visualizeSorting("Selection Sort"));

        JButton mergeSortButton = new JButton("Merge Sort");
        mergeSortButton.addActionListener(e -> panel.visualizeSorting("Merge Sort"));

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> panel.stopSorting = true); // Set stop flag to true

        controlPanel.add(sizeLabel);
        controlPanel.add(sizeSlider);
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        controlPanel.add(orderCheckBox);
        controlPanel.add(newArrayButton);
        controlPanel.add(bubbleSortButton);
        controlPanel.add(selectionSortButton);
        controlPanel.add(mergeSortButton);
        controlPanel.add(stopButton); // Add the stop button to the control panel

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.NORTH);

        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
