/**
 * Created by Анна on 21.05.2014.
 */

import com.sun.org.apache.xpath.internal.SourceTree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractBufferedImageOp implements BufferedImageOp, Cloneable {

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if (dstCM == null)
            dstCM = src.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }

    public static BufferedImage enlarge(BufferedImage image, int n) {


        int w = n * image.getWidth();

        int h = n * image.getHeight();


        BufferedImage enlargedImage =

                new BufferedImage(w, h, image.getType());


        for (int y = 0; y < h; ++y)

            for (int x = 0; x < w; ++x)

                enlargedImage.setRGB(x, y, image.getRGB(x / n, y / n));


        return enlargedImage;

    }

    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null)
            dstPt = new Point2D.Double();
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     *
     * @param image  a BufferedImage object
     * @param x      the left edge of the pixel block
     * @param y      the right edge of the pixel block
     * @param width  the width of the pixel arry
     * @param height the height of the pixel arry
     * @param pixels the array to hold the returned pixels. May be null.
     * @return the pixels
     * @see #setRGB
     */
    public int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
            return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     *
     * @param image  a BufferedImage object
     * @param x      the left edge of the pixel block
     * @param y      the right edge of the pixel block
     * @param width  the width of the pixel arry
     * @param height the height of the pixel arry
     * @param pixels the array of pixels to set
     * @see #getRGB
     */
    public void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
            image.getRaster().setDataElements(x, y, width, height, pixels);
        else
            image.setRGB(x, y, width, height, pixels, 0, width);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

class ImageDrawingComponent extends Component {

    static String descs[] = {
            "Снимок 1",
            "Снимок 2",
            "Снимок 3",
            "Снимок 4",
            "Снимок 5",
            "Снимок 6",
            "Снимок 7",
            "Снимок 8",
            "Снимок 9",
            "Снимок 10",

    };

    int opIndex;
    private BufferedImage bi;
    Image bi1;

    int w, h;

    /*public static final float[] SHARPEN3x3 = { // sharpening filter kernel
            0.f, -1.f, 0.f,
            -1.f, 5.3f, -1.f,
            0.f, -1.f, 0.f
    };

    public static final float[] BLUR5x5 = {

            /*
            0.015118251f, 0.0440927077f, 0.0650479183f, 0.0440927077f, 0.015118251f,
            0.0440927077f, 0.0604992967f, 0.0771162265f, 0.0604992967f, 0.0440927077f,
            0.0650479183f, 0.0771162265f, 0.090748945f, 0.0771162265f, 0.0650479183f,
            0.0440927077f, 0.0604992967f, 0.0771162265f, 0.0604992967f, 0.0440927077f,
            0.015118251f, 0.0440927077f, 0.0650479183f, 0.0440927077f, 0.015118251f

**//*

            0.0625f, 0.25f, 0.0625f,    // low-pass filter kernel
            0.25f, 0.375f, 0.25f,
            0.0625f, 0.25f, 0.0625f

    }*/

    public ImageDrawingComponent(URL imageSrc) {
        try {
            bi = ImageIO.read(imageSrc);
            bi1 = ImageIO.read(imageSrc);
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 =
                        new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                bi = bi2;
            }
        } catch (IOException e) {
            System.out.println("Image could not be read");
//            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }

    static String[] getDescriptions() {
        return descs;
    }

    void setOpIndex(int i) {
        opIndex = i;
    }

    /* In this example the image is recalculated on the fly every time
     * This makes sense where repaints are infrequent or will use a
     * different filter/op from the last.
     * In other cases it may make sense to "cache" the results of the
     * operation so that unless 'opIndex' changes, drawing is always a
     * simple copy.
     * In such a case create the cached image and directly apply the filter
     * to it and retain the resulting image to be repainted.
     * The resulting image if untouched and unchanged Java 2D may potentially
     * use hardware features to accelerate the blit.
     */
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        switch (opIndex) {
            case 0: /* copy */
                g.drawImage(bi, 0, 0, null);
                break;

            case 1:

            case 2:

            case 3:

            case 4:
            case 5:
            case 6:

            case 7:
            default:
        }
    }
}

public final class ImageDrawingApplet extends JApplet {
    final static List<String> list = new ArrayList<>();
    private static String imageFileName = "C:/Users/Анна/Desktop/MRI/circ.png";
    private URL imageSrc;

    public ImageDrawingApplet(URL imageSrc) {
        this.imageSrc = imageSrc;
        list.add(imageFileName);
        for (int i = 0; i < 25; i++) {
            list.add("C:/Users/Анна/Desktop/MRI/"+ (i + 1)+ ".jpg");
        }
    }

    public void init() {
        try {
            imageSrc = new URL(getCodeBase(), imageFileName);
        } catch (MalformedURLException e) {
            Logger.getLogger(getClass().getName()).log(Level.CONFIG, e.getMessage(), e);
        }
        buildUI();
    }

    public void buildUI() {
        final ImageDrawingComponent id = new ImageDrawingComponent(imageSrc);
        add("Center", id);
        JComboBox choices = new JComboBox(id.getDescriptions());
        choices.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                id.setOpIndex(cb.getSelectedIndex());
                id.repaint();
            }

            ;
        });
        add("South", choices);
    }

    public static void main(String s[]) {
        JFrame f = new JFrame("ImageDrawing");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        f.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("x = " + e.getX());
            }
        });
        URL imageSrc = null;
        try {
            imageSrc = ((new File(imageFileName)).toURI()).toURL();
        } catch (MalformedURLException e) {
        }
        ImageDrawingApplet id = new ImageDrawingApplet(imageSrc);
        id.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("POINT x = " + e.getX() + "  y = " + e.getY());
                int[][] array = Converter.convertString(list.get(0));
                int numOfPoint = (new Filters()).getSquare(e.getX(), e.getY(), array);
                System.out.println("значение: " + array[e.getX()][e.getY()]);
                /*for(int i = 0; i < 10; i++){
                    System.out.println(list.get(i));
                    int[][] array = Converter.convertString(list.get(i));
                   // Filters.getSquare(array, e.getY());
                    Filters f= new Filters();

                    f.nonStaticGetSquare();
                    System.out.println(array.toString());
                    System.out.println("значение: " + array[e.getX()][e.getY()]);
                    int x=e.getX();
                    int y=e.getY();
                }*/
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        id.buildUI();
        f.add("Center", id);
        f.pack();
        f.setVisible(true);
    }
}


