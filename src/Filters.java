import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Filters {
    int y=1;
    int x=1;
    int[][] mas;
    int[][] ok;
    int[][] check;
    int s=0;

    public int getSquare(int x, int y, int[][] mas1){
        mas = mas1;
        ok = new int[mas1.length][mas1[0].length];
        check = new int[mas1.length][mas1[0].length];
        List<Point> points =  new ArrayList<>();
        int counter = 0;
        System.out.println(mas1[x][y]);
        points.add(new Point(x,y, mas1[x][y]));
        check[x][y] = 1;// занести с массив ПРОВЕРЯЛИ
        while(points.size()!=0){
            if(points.get(0).value < 128){
                ok[x][y] = 1;// занести точку в массив ОК
                counter++;
                adder(points, points.get(0));
            }
            points.remove(0);
        }
        for (int m = 0; m < ok.length; m++){
            for (int n = 0; n < ok[0].length; n++){
                if (ok[m][n] !=0) {
                    s = s + 1;
                }
            }

        }
        return s;
    }

    private void adder(List<Point> list, Point point){
        // перед добавлением проверить не проверяли ли ?
        if(check[point.x + 1][point.y] != 1 ){
            list.add(new Point(point.x + 1, point.y, mas[point.x + 1][point.y]));
            check[point.x + 1][point.y] = 1; // добавить точку в ПРОВЕРЯЛИ
        }
        if(check[point.x - 1][point.y] != 1 ) {
            list.add(new Point(point.x - 1, point.y, mas[point.x - 1][point.y]));
            check[point.x - 1][point.y] = 1;
        }
        if(check[point.x][point.y - 1] != 1 ) {
            list.add(new Point(point.x, point.y - 1, mas[point.x][point.y - 1]));
            check[point.x][point.y - 1] = 1;
        }
        if(check[point.x][point.y + 1] != 1 ) {
            list.add(new Point(point.x, point.y + 1, mas[point.x][point.y + 1]));
            check[point.x][point.y + 1] = 1;
        }
    }

    class Point{
        int x;
        int y;
        int value;
        public Point(int x,int y, int value){
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }
}
