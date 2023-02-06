import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Branch {
    List<Point> branch = new ArrayList<>();

    public Branch(){ }

    public Point head (){
        return branch.get(branch.size() - 1);
    }

    public void draw(Graphics g, double scale){
        for (Point point : branch) {
            g.fillRect((int) (point.x * scale) + 50, (int) (point.y * scale) + 50, (int) scale, (int) scale);
        }
    }
}
