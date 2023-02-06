import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

import static java.awt.Color.*;

public class Window extends JPanel implements ActionListener {

    Timer timer;
    public static final int boardSizeX = 1000;
    public static final int boardSizeY = 700;

    int[][] cycle = {{1, -1, 0, 0}, {0, 0, 1, -1}};

    boolean run = false;
    boolean solved = false;
    boolean drawActiveBranches = true;

    Point startPoint;
    Point endPoint;

    Set<Branch> branches = new HashSet<>();
    Set<Branch> nextIter = new HashSet<>();
    Branch solvePath = new Branch();

    DrawMode mode = DrawMode.BLOCK;

    int unitSize = 30;
    int[][] board = new int[unitSize][unitSize];


    public Window(){
        this.setPreferredSize(new Dimension(boardSizeX, boardSizeY));
        this.setBackground(new Color(0, 0, 0, 255));
        this.setFocusable(true);
        this.addMouseListener(new ClickListener());
        this.addMouseMotionListener(new MouseTracker());
        this.addKeyListener(new KeyListener());
        timer = new Timer(100, this);
        start();
    }

    public void start() {
        setup();
        timer.start();
    }

    public void setup() {
        startPoint = new Point(1, 1);
        endPoint = new Point(board.length - 2, board.length - 2);
        makeWalls();
        initBranches();
    }

    public void clearBranches(){
        branches.clear();
        solvePath.branch.clear();
        nextIter.clear();
    }

    public void initBranches(){
        clearBranches();
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] == -1) board[i][j] = 0;
            }
        }

        Branch startBranch = new Branch();
        startBranch.branch.add(new Point(startPoint.x, startPoint.y));
        branches.add(startBranch);
    }

    public void update(){
        nextIter = new HashSet<>();

        branches.forEach(p -> {
            for(int i = 0; i < 4; i++){
                if(board[p.head().x + cycle[0][i]][p.head().y + cycle[1][i]] == 0 && safe(new Point(p.head().x + + cycle[0][i], p.head().y + cycle[1][i]))){
                    nextIter.add(create(p, new Point(p.head().x + + cycle[0][i], p.head().y + cycle[1][i])));
                }
            }
        });

        branches.forEach(b -> board[b.head().x][b.head().y] = -1);
        nextIter.forEach(b -> board[b.head().x][b.head().y] = -1);

        branches.clear();
        branches = nextIter;

        for (Branch branch : branches) {
            if(branch.head().x == endPoint.x && branch.head().y == endPoint.y){
                run = false;
                solved = true;
                solvePath = branch;
            }
        }

        if(branches.size() == 0){
            run = false;
            solved = true;
            System.out.println("no solution found");
        }
    }

    public boolean safe(Point p) {
        if(board[p.x][p.y] == 1) return false;
        for(Branch b : nextIter){
            if (b.head().x == p.x && b.head().y == p.y) return false;
        };
        for(Branch b : branches){
            if (b.head().x == p.x && b.head().y == p.y) return true;
        };

        return true;
    }

    public Branch create(Branch b, Point newPoint){
        Branch branch = new Branch();
        branch.branch.addAll(b.branch);
        branch.branch.add(newPoint);
        return branch;
    }

    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        g.setFont(new Font("Arial", Font.BOLD, 30));

        double scale = 600f / board.length;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                g.setColor(switch (board[i][j]) {
                    case 0 -> black;
                    case 1 -> white;
                    case 2 -> GREEN;
                    case 4 -> new Color(47, 47, 47, 255);
                    case -1 -> new Color(0, 166, 255);
                    default -> null;
                });
                g.fillRect((int) (i * scale) + 50, (int) (j * scale) + 50, (int) scale, (int) scale);
                g.setColor(gray);
                g.drawRect((int) (i * scale) + 50, (int) (j * scale) + 50, (int) scale, (int) scale);
            }
        }

        g.setColor(red);{
            if(drawActiveBranches){
                branches.forEach(b -> b.draw(g, scale));
            }
        }

        g.setColor(green);
        solvePath.draw(g, scale);
        g.fillRect((int) (startPoint.x * scale) + 50, (int) (startPoint.y * scale) + 50, (int) scale, (int) scale);
        g.setColor(red);
        g.fillRect((int) (endPoint.x * scale) + 50, (int) (endPoint.y * scale) + 50, (int) scale, (int) scale);
        g.setColor(white);
        g.drawRect(700, 600, 250, 50);
        g.drawString("RUN", 700 + ((250 - g.getFontMetrics().stringWidth("RUN")) / 2), 635);
        g.drawRect(700, 50, 250, 50);
        g.drawString("Draw Mode", 700 + ((250 - g.getFontMetrics().stringWidth("Draw Mode")) / 2), 85);
        g.drawString("Current Mode:", 700 + ((250 - g.getFontMetrics().stringWidth("Current Mode:")) / 2), 140);
        String s = switch(mode){
            case BLOCK -> "Draw Block";
            case START -> "Move Start";
            case END -> "Move Goal";
        };
        g.drawString(s, 700 + ((250 - g.getFontMetrics().stringWidth(s)) / 2), 180);

        g.setColor(drawActiveBranches ? green : red);
        g.drawString("Draw Active Branches", 700 + ((250 - g.getFontMetrics().stringWidth("Draw Active Branches")) / 2), 250);
    }

    public void makeWalls(){
        for(int i = 0; i < board.length; i++){
            board[0][i] = 4;
            board[board.length - 1][i] = 4;
            board[i][0] = 4;
            board[i][board.length - 1] = 4;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        if(run) update();
    }

    public class ClickListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if(x > 50 && x < 650 && y > 50 && y < 650){
                int i = board[(x - 50) / (600 / unitSize)][(y - 50) / (600 / unitSize)];
                switch (mode) {
                    case BLOCK -> board[(x - 50) / (600 / unitSize)][(y - 50) / (600 / unitSize)] = switch (i) {
                        case 1 -> 0;
                        case 4 -> 4;
                        default -> 1;
                    };
                    case START -> {
                        if (i != 4 && !(endPoint.x == (x - 50) / (600 / unitSize) && endPoint.y == (y - 50) / (600 / unitSize))) {
                            startPoint.x = (x - 50) / (600 / unitSize);
                            startPoint.y = (y - 50) / (600 / unitSize);
                            initBranches();
                        }
                    }
                    case END -> {
                        if (i != 4 && !(startPoint.x == (x - 50) / (600 / unitSize) && startPoint.y == (y - 50) / (600 / unitSize))) {
                            endPoint.x = (x - 50) / (600 / unitSize);
                            endPoint.y = (y - 50) / (600 / unitSize);
                            initBranches();
                        }
                    }
                }
            }

            if(x > 700 && x < 950 && y > 600 && y < 650){
                if(solved){
                    initBranches();
                    solved = false;
                }
                run = true;
            }
            if(x > 700 && x < 950 && y > 50 && y < 100){
                mode = switch(mode){
                    case BLOCK -> DrawMode.START;
                    case START -> DrawMode.END;
                    case END -> DrawMode.BLOCK;
                };
            }
            if(x > 700 && x < 950 && y > 220 && y < 250){
                drawActiveBranches = !drawActiveBranches;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    public class MouseTracker implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if(x > 50 && x < 650 && y > 50 && y < 650){
                int i = board[(x - 50) / (600 / unitSize)][(y - 50) / (600 / unitSize)];
                switch (mode) {
                    case BLOCK -> board[(x - 50) / (600 / unitSize)][(y - 50) / (600 / unitSize)] = (i == 4) ? 4 : 1;
                    case START -> {
                        if (i != 4 && !(endPoint.x == (x - 50) / (600 / unitSize) && endPoint.y == (y - 50) / (600 / unitSize))) {
                            startPoint.x = (x - 50) / (600 / unitSize);
                            startPoint.y = (y - 50) / (600 / unitSize);
                            initBranches();
                        }
                    }
                    case END -> {
                        if (i != 4 && !(startPoint.x == (x - 50) / (600 / unitSize) && startPoint.y == (y - 50) / (600 / unitSize))) {
                            endPoint.x = (x - 50) / (600 / unitSize);
                            endPoint.y = (y - 50) / (600 / unitSize);
                            initBranches();
                        }
                    }
                }
            }
        }
        @Override
        public void mouseMoved(MouseEvent e) {}
    }
    public class KeyListener implements java.awt.event.KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_C) {
                board = new int[unitSize][unitSize];
                clearBranches();
                setup();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    enum DrawMode {
        BLOCK,
        START,
        END
    }
}