package org.fatmansoft.teach.util;

public class FormatJudge {
    public static boolean IdCard(String str) {
        if(str.length()!=18){
            return false;
        }
        //创建数组arr[]存放17位系数
        int arr[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        //创建数组arrCheck[]存放校验码，元素对应余数为数组下标，88为大写'X'字符的ASCII码
        int arrCheck[] = {1, 0, 88, 9, 8, 7, 6, 5, 4, 3, 2};
        //计算身份证前17位与17位系数乘积的总和
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            char c = str.charAt(i);
            int a = c - 48;
            sum += a * arr[i];
        }
        //身份证为X结尾
        if (str.charAt(17) == 88) {
            //总和sum对11取余后通过数组arrCheck[]校验
            if (arrCheck[sum % 11] == 88)
                return true;
            else
                return false;
        }
        //身份证为阿拉伯数字结尾
        else {
            //将身份证最后一位数字字符转换为int型
            char c = str.charAt(17);
            //总和sum对11取余后通过数组arrCheck[]校验
            if (arrCheck[sum % 11] == c - 48)
                return true;
            else
                return false;
        }
    }

    public static boolean birthday(String birthday,String card){
        //2000-10-10
        String day = card.substring(6,14);
        String day0 = birthday.substring(0,4)+birthday.substring(5,7)+birthday.substring(8,10);
        if(day.equals(day0)){
            return true;
        }else{
            return false;
        }
    }

//    public static void main(String[] args){
//        String s= "370283200411297024";
//        String b = "2004-11-29";
//        birthday(b,s);
//        System.out.println(IdCard(s));
//    }
}
