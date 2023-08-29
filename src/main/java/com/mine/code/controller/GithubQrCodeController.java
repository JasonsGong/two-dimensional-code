package com.mine.code.controller;

import com.github.hui.quick.plugin.qrcode.wrapper.QrCodeDeWrapper;
import com.github.hui.quick.plugin.qrcode.wrapper.QrCodeGenWrapper;
import com.github.hui.quick.plugin.qrcode.wrapper.QrCodeOptions;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jason Gong
 * @version 1.0
 * @website https://jasonsgong.gitee.io
 * @Date 2023/8/29
 * @Description 使用qrcode生成二维码
 */
@Controller
public class GithubQrCodeController {
    /**
     * 编写请求跳转到使用qrcode生成二维码的页面
     */
    @GetMapping("/qrcode")
    public String toQrCode() {
        return "github-qrcode";
    }


    /**
     * 使用qrcode生成黑白二维码
     */
    @PostMapping("/generateWithQrCode")
    public String generateWithQrCode(@RequestParam("url") String url, HttpServletResponse response, HttpServletRequest request) {
        try {
            //生成二维码
            //生成黑白的二维码
            //BufferedImage image = QrCodeGenWrapper.of(url).asBufferedImage();

            //生成带logo的黑白二维码
//          InputStream inputStream = request.getPart("logo").getInputStream();
//            BufferedImage image = QrCodeGenWrapper.of(url)
//                    .setLogo(inputStream)//logo图片的输入流
//                    .setLogoRate(7)//设置logo图片和二维码之间的比例，7表示logo的宽度等于二维码的1/7
//                    .setLogoStyle(QrCodeOptions.LogoStyle.ROUND)//设置logo图片的样式，将logo的边框形状设置成圆形
//                    .asBufferedImage();

            //生成彩色的二维码
            //BufferedImage image = QrCodeGenWrapper.of(url).setDrawPreColor(Color.GREEN).asBufferedImage();


//            //生成带有背景图的二维码
//            InputStream inputStream = request.getPart("logo").getInputStream();
//            BufferedImage image = QrCodeGenWrapper.of(url)
//                    .setBgImg(inputStream)
//                    .setBgOpacity(0.5F)//设置透明度
//                    .asBufferedImage();

//            //生成特殊形状的二维码
//            BufferedImage image = QrCodeGenWrapper.of(url)
//                    .setDrawEnableScale(true)//启用二维码绘制时的缩放功能
//                    .setDrawStyle(QrCodeOptions.DrawStyle.DIAMOND)//绘制钻石形状的二维码
//                    .asBufferedImage();

            //生成图片填充二维码
            InputStream inputStream = request.getPart("logo").getInputStream();
            BufferedImage image = QrCodeGenWrapper.of(url)
                    .setErrorCorrection(ErrorCorrectionLevel.H)//设置二维码的纠正级别
                    .setDrawStyle(QrCodeOptions.DrawStyle.IMAGE)//绘制二维码的时候采用图片填充
                    .addImg(1, 1, inputStream)//添加图片
                    .asBufferedImage();
            //将生成的二维码响应到浏览器
            ImageIO.write(image, "png", response.getOutputStream());
            //关闭流
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
