import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Converter {
    public static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }
        int[][] norm  = normalize(result);
        return norm;
    }

    public static int[][] convertString(String string){
        int[][] array = new int[0][];
        try {
            array  = convertTo2DUsingGetRGB(ImageIO.read(new File(string)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    private static int[][] normalize(int[][] result){
        int[][] norm;
        int min = 0;
        norm = result;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++){
                if (min >result[i][j]){
                    min = result[i][j];
                }
            }
        }
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++){
                norm[i][j]= (int) (1.*result[i][j]/min*1000);
            }
        }


        return norm;
    }
}
