import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class SortingVisualize extends JPanel {
    private int[] array;
    private int delay = 50;
    private final Random rand = new Random();

    public SortingVisualize(int size) {
        array = new int[size];
        generateNewArray(size);  // Call with the initial size
    }

    public void generateNewArray(int newLength) {
        // Update the array length based on the user input
        array = new int[newLength];

        // Populate the array with random values
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(200);
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
        int barWidth = width / array.length;

        g.setFont(new Font("Arial", Font.BOLD, 16));
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
        }).start();
    }

    private void bubbleSort() {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] < array[j + 1]) {  // Change to descending order
                    swap(j, j + 1);
                }
                repaintAndWait();
            }
        }
    }

    private void selectionSort() {
        for (int i = 0; i < array.length - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] > array[maxIndex]) {  // Change to descending order
                    maxIndex = j;
                }
            }
            swap(i, maxIndex);
            repaintAndWait();
        }
    }

    private void mergeSort(int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (array[i] >= array[j]) {  // Change to descending order
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = array[i++];
        }

        while (j <= right) {
            temp[k++] = array[j++];
        }

        for (i = left, k = 0; i <= right; i++, k++) {
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
        SortingVisualize panel = new SortingVisualize(50);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(Color.DARK_GRAY);

        JLabel sizeLabel = new JLabel("Size: ");
        sizeLabel.setForeground(Color.WHITE);
        JSlider sizeSlider = new JSlider(10, 100, 50);
        sizeSlider.addChangeListener(e -> {
            panel.generateNewArray(sizeSlider.getValue());
        });

        JLabel speedLabel = new JLabel("Speed: ");
        speedLabel.setForeground(Color.WHITE);
        JSlider speedSlider = new JSlider(10, 200, 50);
        speedSlider.addChangeListener(e -> panel.delay = speedSlider.getValue());

        JButton newArrayButton = new JButton("New Array");
        newArrayButton.addActionListener(e -> panel.generateNewArray(panel.array.length));

        JButton bubbleSortButton = new JButton("Bubble Sort");
        bubbleSortButton.addActionListener(e -> panel.visualizeSorting("Bubble Sort"));

        JButton selectionSortButton = new JButton("Selection Sort");
        selectionSortButton.addActionListener(e -> panel.visualizeSorting("Selection Sort"));

        JButton mergeSortButton = new JButton("Merge Sort");
        mergeSortButton.addActionListener(e -> panel.visualizeSorting("Merge Sort"));

        JButton setArrayLengthButton = new JButton("Set Array Length");
        setArrayLengthButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter array length:");
            try {
                int length = Integer.parseInt(input);
                if (length > 0) {
                    panel.generateNewArray(length); // Pass the user input as the array length
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a positive number.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid number.");
            }
        });

        controlPanel.add(sizeLabel);
        controlPanel.add(sizeSlider);
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        controlPanel.add(newArrayButton);
        controlPanel.add(bubbleSortButton);
        controlPanel.add(selectionSortButton);
        controlPanel.add(mergeSortButton);
        controlPanel.add(setArrayLengthButton);  // Add the new button

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.NORTH);

        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
