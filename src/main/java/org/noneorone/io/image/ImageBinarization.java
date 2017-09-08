package org.noneorone.io.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
* Title: JavaTech<br>
* Description: Image Binarization<br>
* Copyright: Copyright (c) 2011 <br>
* Create DateTime: Jul 15, 2011 9:40:27 AM <br>
* @author wangmeng(C)
*/
public class ImageBinarization {

    BufferedImage image;
    private int iw, ih;
    private int[] pixels;


    public ImageBinarization(BufferedImage image) {
    	this.image = image;
        iw = image.getWidth();
        ih = image.getHeight();
        pixels = new int[iw * ih];

    }

    /**
     * Thresholding,change the grey of the image.
     * @return BufferedImage 
     */
    public BufferedImage changeGrey() {
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, pixels, 0, iw);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Set the thresholding,default value is 100.
        int grey = 100;
        // ��ͼ����ж�ֵ��������Alphaֵ���ֲ���
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 0; i < iw * ih; i++) {
            int red, green, blue;
            int alpha = cm.getAlpha(pixels[i]);
            if (cm.getRed(pixels[i]) > grey) {
                red = 255;
            } else {
                red = 0;
            }
            if (cm.getGreen(pixels[i]) > grey) {
                green = 255;
            } else {
                green = 0;
            }
            if (cm.getBlue(pixels[i]) > grey) {
                blue = 255;
            } else {
                blue = 0;
            }
            pixels[i] = alpha << 24 | red << 16 | green << 8 | blue; //ͨ����λ���¹���ĳһ�����ص�RGBֵ
        }
        // �������е����ز���һ��ͼ��
        Image tempImg=Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw,ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null),tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR );
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;

    }
    
    /** ��ֵ�˲� */
    public BufferedImage getMedian() {
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, pixels, 0, iw);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // ��ͼ�������ֵ�˲���Alphaֵ���ֲ���
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 1; i < ih - 1; i++) {
            for (int j = 1; j < iw - 1; j++) {
                int red, green, blue;
                int alpha = cm.getAlpha(pixels[i * iw + j]);

                // int red2 = cm.getRed(pixels[(i - 1) * iw + j]);
                int red4 = cm.getRed(pixels[i * iw + j - 1]);
                int red5 = cm.getRed(pixels[i * iw + j]);
                int red6 = cm.getRed(pixels[i * iw + j + 1]);
                // int red8 = cm.getRed(pixels[(i + 1) * iw + j]);

                // ˮƽ���������ֵ�˲�
                if (red4 >= red5) {
                    if (red5 >= red6) {
                        red = red5;
                    } else {
                        if (red4 >= red6) {
                            red = red6;
                        } else {
                            red = red4;
                        }
                    }
                } else {
                    if (red4 > red6) {
                        red = red4;
                    } else {
                        if (red5 > red6) {
                            red = red6;
                        } else {
                            red = red5;
                        }
                    }
                }

                int green4 = cm.getGreen(pixels[i * iw + j - 1]);
                int green5 = cm.getGreen(pixels[i * iw + j]);
                int green6 = cm.getGreen(pixels[i * iw + j + 1]);

                // ˮƽ���������ֵ�˲�
                if (green4 >= green5) {
                    if (green5 >= green6) {
                        green = green5;
                    } else {
                        if (green4 >= green6) {
                            green = green6;
                        } else {
                            green = green4;
                        }
                    }
                } else {
                    if (green4 > green6) {
                        green = green4;
                    } else {
                        if (green5 > green6) {
                            green = green6;
                        } else {
                            green = green5;
                        }
                    }
                }

                // int blue2 = cm.getBlue(pixels[(i - 1) * iw + j]);
                int blue4 = cm.getBlue(pixels[i * iw + j - 1]);
                int blue5 = cm.getBlue(pixels[i * iw + j]);
                int blue6 = cm.getBlue(pixels[i * iw + j + 1]);
                // int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]);

                // ˮƽ���������ֵ�˲�
                if (blue4 >= blue5) {
                    if (blue5 >= blue6) {
                        blue = blue5;
                    } else {
                        if (blue4 >= blue6) {
                            blue = blue6;
                        } else {
                            blue = blue4;
                        }
                    }
                } else {
                    if (blue4 > blue6) {
                        blue = blue4;
                    } else {
                        if (blue5 > blue6) {
                            blue = blue6;
                        } else {
                            blue = blue5;
                        }
                    }
                }
                pixels[i * iw + j] = alpha << 24 | red << 16 | green << 8 | blue;
            }
        }

        // �������е����ز���һ��ͼ��
        Image tempImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw,ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null),tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR );
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;

    }




    public BufferedImage getGrey() {
        ColorConvertOp ccp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return image = ccp.filter(image,null);
    }

    //Brighten using a linear formula that increases all color values
    public BufferedImage getBrighten() {
        RescaleOp rop = new RescaleOp(1.25f, 0, null);
        return image = rop.filter(image,null);
    }
    //Blur by "convolving" the image with a matrix
    public BufferedImage getBlur() {
        float[] data = {
                       	.1111f, .1111f, .1111f,
                       	.1111f, .1111f, .1111f,
                       	.1111f, .1111f, .1111f, 
                       };
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
        return image = cop.filter(image,null);

    }

    // Sharpen by using a different matrix
    public BufferedImage getSharpen() {
        float[] data = {
                       	0.0f, -0.75f, 0.0f,
                       	-0.75f, 4.0f, -0.75f,
                       	0.0f, -0.75f, 0.0f
                       };
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
        return image = cop.filter(image,null);
    }
    
    // 11) Rotate the image 180 degrees about its center point
    public BufferedImage getRotate() {
        AffineTransformOp atop = new AffineTransformOp(AffineTransform.getRotateInstance(Math.PI,image.getWidth()/2,image.getHeight()/2),
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return image=atop.filter(image,null);
    }
    
    public BufferedImage getProcessedImg()
    {
        return image;
    }

    /**
     * Filter the image pixel.
     * @param pic
     * @param suffix
     */
    public void filter(String pic, String suffix){
        FileInputStream fis = null;
        BufferedImage bi = null;
        String picName = null;
        File file = null;
        try {
			fis = new FileInputStream(pic);
			bi = ImageIO.read(fis);
			
	        changeGrey();
	        getGrey();
	        getBrighten();
	        bi = getProcessedImg();
	        picName = pic.substring(0,pic.lastIndexOf("."));
	        file = new File(picName + "." + suffix);
	        ImageIO.write(bi, suffix, file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

    }

}

