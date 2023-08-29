package com.mine.code.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Gong
 * @version 1.0
 * @website https://jasonsgong.gitee.io
 * @Date 2023/8/28
 * @Description
 */
@Slf4j
@Controller
public class CodeController {


    /**
     * 跳转到生成带logo的黑白二维码
     */
    @GetMapping("/logo")
    public String toLogo() {
        return "qrcode";
    }

    /**
     * 生成带logo的黑白二维码
     */
    @PostMapping("/generateWithLogo")
    public String generateWithLogo(@RequestParam("url") String url, HttpServletResponse response, HttpServletRequest request) {
        try {
            Map map = new HashMap<>();
            map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            map.put(EncodeHintType.CHARACTER_SET, "utf-8");
            map.put(EncodeHintType.MARGIN, 1);
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300, map);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            //给二维码添加logo
            //1.获取logo
            Part logoPart = request.getPart("logo");
            InputStream inputStream = logoPart.getInputStream();
            BufferedImage logoImage = ImageIO.read(inputStream);
            //2.对获取的logo图片进行缩放
            int logoWidth = logoImage.getWidth(null);
            int logoHeight = logoImage.getHeight(null);
            if (logoWidth > 60){
                logoWidth = 60;
            }
            if (logoHeight > 60){
                logoHeight = 60;
            }
            //使用平滑缩放算法对原始的logo图像进行缩放到一个全新的图像
            Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            //3.将缩放的图片画在黑白的二维码上
            //获取一个画笔
            Graphics2D graphics2D = image.createGraphics();
            //计算从哪里开始画 300指的是二维码的宽度和高度
            int x = (300 - logoWidth) /2;
            int y = (300 - logoHeight) /2;
            //画上去
            graphics2D.drawImage(scaledLogo,x,y,null);
            //实现logo的圆角效果
            Shape shape = new RoundRectangle2D.Float(x, y, logoWidth, logoHeight, 10, 10);
            //使用一个宽度为4像素的基本笔触
            graphics2D.setStroke(new BasicStroke(4f));
            //给logo画圆角矩形
            graphics2D.draw(shape);
            //释放画笔
            graphics2D.dispose();
            //将二维码响应到浏览器
            ImageIO.write(image, "png", response.getOutputStream());
            //关闭流
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 跳转到生成普通黑白二维码的页面
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 生成普通的黑白二维码
     */
    @GetMapping("/generate")
    public String generate(@RequestParam("url") String url, HttpServletResponse response) {
        log.info("文本内容:{}", url);
        try {
            //创建一个map集合，存储二维码的相关属性
            Map map = new HashMap<>();
            //EncodeHintType 编码的提示类型
            //设置二维码的误差校正级别 可选值有 L(7%) M(15%) Q(25%) H(30%)
            //选择L级别的容错率，相当于允许二维码在整体的颜色区域中，最多有7%的坏像素点;择H级别的容错率，相当于允许二维码在整体的颜色区域中，最多有30%的坏像素点
            map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置二维码的字符集
            map.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //设置二维码四周的留白 1表示1像素
            map.put(EncodeHintType.MARGIN, 1);
            //创建zxing的核心对象MultiFormatWriter (多格式写入器)
            //通过MultiFormatWriter对象来生成二维码
            MultiFormatWriter writer = new MultiFormatWriter();
            //参数一:内容
            //参数二:二维码格式
            //BarcodeFormat(码格式) QR_CODE ：常见的二维码格式之一，广泛应用于商品包装、扫码支付
            //AZTEC_CODE：高密度，可靠性很高 容错率更低 储存个人信息、证件信息、账户密码
            //PDF417 可以存储大量的信息 数据密度高 应用于航空机票、配送标签、法律文件
            //DATA_MATRIX: 小巧的二维码格式 编码格式类似于QR_CODE 但是优于QR_CODE 适合嵌入简单的产品标签 医疗图像 检测数据
            //参数三四:二维码的宽度和高度
            //参数五:二维码参数
            //位矩阵对象 （位矩阵对象对象的内部实际上是一个二位数组，二维数组中每一个元素是boolean类型 true代表黑色 false代表白色）
            BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300, map);
            //获取矩阵的宽度和高度
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            //生成二维码图片
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            //遍历位矩阵对象
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    //设置每一块的颜色值
                    //0xFF000000表示黑色  0xFFFFFFFF表示白色
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            //将图片响应到客户端
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(image, "png", out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
