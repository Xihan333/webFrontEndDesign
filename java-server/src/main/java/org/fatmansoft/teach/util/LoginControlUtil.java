package org.fatmansoft.teach.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class LoginControlUtil {
    /** 验证码图片的宽度 */
    private int width = 54;
    /** 验证码图片的高度 */
    private int height = 20;
    /** 验证码字符个数 */
    private int codeCount = 4;
    // 字符间距
    private int x = width / (codeCount + 1);
    // 字体高度
    private int fontHeight=height - 2;
    private int codeY=height - 4;

    private char[] codeSequence = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private Random random = new Random();

    private Map<Integer, String> codeMap = new HashMap<Integer, String>();
    private Map<String, Object[]> msgCodeMap = new HashMap<String, Object[]>();
    private Map<String, Integer> loginCountMap = new HashMap<String, Integer>();
    private static LoginControlUtil instance = new LoginControlUtil();
    public static LoginControlUtil getInstance(){
        return instance;
    }
    public Map getValidateCodeDataMap(){
        BufferedImage buffImg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();

        // 创建一个随机数生成器类

        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.PLAIN | Font.BOLD, fontHeight);
        // 设置字体。
        g.setFont(font);

        // 画边框。
//        g.setColor(Color.BLACK);
//        g.drawRect(0, 0, width - 1, height - 1);

        // 随机产生160条干扰线，使图象中的认证码不易被其它程序探测到。
/*        g.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
*/
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String strRand = String.valueOf(codeSequence[random.nextInt(10)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
//            red = random.nextInt(255);
//            green = random.nextInt(255);
//            blue = random.nextInt(255);

            // 用随机产生的颜色将验证码绘制到图像中。
//            g.setColor(new Color(red, green, blue));
            g.setColor(new Color(147, 14,20));
            g.drawString(strRand, (i + 1) * x - 6, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        Integer validateCodeId = random.nextInt();
        String  validateCode = randomCode.toString();
        codeMap.put(validateCodeId, validateCode);
        Map data = new HashMap();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(buffImg, "jpeg", out);
            out.close();
            String imgStr = "data:image/png;base64,";
            String s = new String(Base64.getEncoder().encode(out.toByteArray()));
            imgStr = imgStr + s;
            data.put("validateCodeId", validateCodeId);
            data.put("img",imgStr);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    public String getValidateCode(Integer validateCodeId) {
        return codeMap.get(validateCodeId);
    }
    public Integer getLoginCount(String username){
        Integer count = loginCountMap.get(username);
        if(count == null)
            count = 0;
        return count;
    }
    public void setLoginCount(String username, Integer count){
        loginCountMap.put(username,count);
    }
    public void clearLoginCount(String username){
        loginCountMap.remove(username);
    }
    public void clearData(){
        codeMap.clear();
        msgCodeMap.clear();
        loginCountMap.clear();
    }
    public  String createMessageCode(String perNum){
        String randomCode="";
        for (int i = 0; i < codeCount; i++) {
            randomCode+= String.valueOf(codeSequence[random.nextInt(10)]);
        }
        Object []o = new Object[]{randomCode,new Date()};
        msgCodeMap.put(perNum,o);
        return randomCode;
    }
    public Object [] getMessageCodeObject(String perNum){
        return msgCodeMap.get(perNum);
    }
}
