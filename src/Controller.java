
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author NguyenManhCuong
 */
public class Controller {
    
    GUI frame;
    private boolean isRunning = false;
    private int countMove;
    private Timer timer;
    private JButton[][] matrix;
    private int size;
    private int x, y;
    
    public Controller(GUI mainFrame) {
        this.frame = mainFrame;
        createButton();
    }
    
    public void runGame() {
        if (isRunning) {
            timer.stop();
            int confirm = JOptionPane.showConfirmDialog(null, "Do you want to make a new game");
            if (confirm == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                timer.start();
            }
        } else {
            resetGame();
        }
    }

    // create button
    private void createButton() {
        String str = frame.getCboSize().getSelectedItem().toString();
        String[] number = str.split("x");
        size = Integer.parseInt(number[0]);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        int col = (int) width / 90;        
        int row = (int) height / 90;        
        if (col < size || row < size) {
            JOptionPane.showMessageDialog(null, "Can't play with this size");
            return;
        }
        matrix = new JButton[size][size];
        // xóa hết các component trên container 
        frame.getPnLayout().removeAll();
        // set lay out với các hàng và 
        //cột đã cho cùng với các khoảng cách 
        //theo chiều dọc và ngang đã xác định.
        frame.getPnLayout().setLayout(new GridLayout(size, size, 15, 15));// 15,15 là khoảng cách giữa các button
        frame.getPnLayout().setPreferredSize(new Dimension(size * 90, size * 90));// set size cho từng button 
        int value = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // gán giá trị cho từng button 
                JButton button = new JButton(String.valueOf(value));
                matrix[i][j] = button;
                frame.getPnLayout().add(button);
                button.addActionListener(new ActionListener() {
                    // đi gán sự kiện cho từng button một 
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isRunning) {
                            // check xem có di chuyển được button hay k
                            if (checkMove(button)) {// nếu đúng 
                                moveButton(button); // thì di chuyên swap button 
                                if (checkWin()) {
                                    isRunning = false;
                                    timer.stop();
                                    JOptionPane.showMessageDialog(null, "You win!");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Press new game to start!");
                        }
                    }
                });
                value++;
            }
        }
        matrix[size - 1][size - 1].setText("");
        mixButton();
        frame.setResizable(false);
        frame.pack();
    }
    
    private void resetGame() {
        resetCountMove();
        resetCountTime();
        createButton();
        isRunning = true;
    }
    
    private void mixButton() {
        x = size - 1;
        y = size - 1;
        for (int i = 0; i < 1000; i++) {
            Random random = new Random();
            int value = random.nextInt(4);
            switch (value) {
                // x - row, y - col
                // Move left
                case 0:
                    if (y > 0) {
                        matrix[x][y].setText(matrix[x][y - 1].getText());
                        matrix[x][y - 1].setText("");
                        y = y - 1;
                    }
                    break;
                //Move up
                case 1:
                    if (x < size - 1) {
                        matrix[x][y].setText(matrix[x + 1][y].getText());
                        matrix[x + 1][y].setText("");
                        x = x + 1;
                    }
                    break;
                // Move right
                case 2:
                    if (y < size - 1) {
                        matrix[x][y].setText(matrix[x][y + 1].getText());
                        matrix[x][y + 1].setText("");
                        y = y + 1;
                    }
                    break;
                //Move down 
                case 3:
                    if (x > 0) {
                        matrix[x][y].setText(matrix[x - 1][y].getText());
                        matrix[x - 1][y].setText("");
                        x = x - 1;
                    }
                    break;
            }
        }
    }
    
    private void resetCountMove() {
        countMove = 0;
        frame.getTxtCountMove().setText(String.valueOf(countMove));
    }
    
    private void resetCountTime() {
        frame.getTxtCountTime().setText("0");
        timer = new Timer(1000, new ActionListener() {
            int second = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                second++;
                frame.getTxtCountTime().setText(String.valueOf(second));
            }
        });
        timer.start();
    }
    
    private Point getPointClicked(JButton button) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j].getText().equalsIgnoreCase(button.getText())) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }
    // check xem 2 button có liền kề nhau hay k 
    private boolean checkMove(JButton button) {
        if (button.getText().equalsIgnoreCase("")) {
            return false;
        }
        Point pClicked = getPointClicked(button);
        if (x == pClicked.x && Math.abs(y - pClicked.y) == 1) {
            return true;
        }
        if (y == pClicked.y && Math.abs(x - pClicked.x) == 1) {
            return true;
        }
        return false;
    }
    
    private void moveButton(JButton button) {
        Point p = getPointClicked(button);
        matrix[x][y].setText(button.getText());
        x = p.x;
        y = p.y;
        button.setText("");
        countMove++;
        frame.getTxtCountMove().setText(String.valueOf(countMove));
    }
    
    private boolean checkWin() {
        if (!matrix[size - 1][size - 1].getText().equalsIgnoreCase("")) {
            return false;
        }
        int value = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == size - 1 && j == size - 1) {
                    return true;
                }
                if (!matrix[i][j].getText().equalsIgnoreCase(String.valueOf(value))) {
                    return false;
                }
                value++;
            }
        }
        return false;
    }
    
}
