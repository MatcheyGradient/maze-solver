import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main extends JFrame {

    //static int[][] map = new int[101][101];

    public Main(){
        this.add(new Window());
        this.setTitle("amazeing");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException {
//        File file = new File("/Users/match/Desktop/maze_map.txt");
//        Scanner s = new Scanner(file);
//
//        int l = 0;
//
//        while (s.hasNext()) {
//            char[] split = s.nextLine().toCharArray();
//
//            for(int i = 0; i < split.length; i++) {
//                map[i][l] = (split[i] == '#') ? 0 : (split[i] == '.') ? 1 : (split[i] == 'S') ? 2 : 3;
//
//                if(split[i] == 'S'){
//                    start = new Point(i ,l);
//                } else if(split[i] == 'G'){
//                    end = new Point(i ,l);
//                }
//            }
//            l++;
//        }

        new Main();
    }

}
