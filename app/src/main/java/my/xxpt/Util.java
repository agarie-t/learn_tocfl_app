package my.xxpt;

import java.util.ArrayList;

import java.util.List;



public class Util {
    public static int No=-1;
    public static List<Data> list;
    public static ArrayList<DataReading> listRead;
    public static List<DataReading2> listRead2;
    public static ArrayList<String> strings;
    public static ArrayList<String> strings2;
    public static String name="b1.jpg";

    public static int T=2000;//从2秒开始播放
    public static int No_=50;//做对25道题弹出是否继续
    public static int No_true=0;//做对题目数
    public static int No_true2=0;//做对题目数



    public static int No2=0;//b num


    public static ArrayList<DataB> list1 = new ArrayList<>();
    public static ArrayList<DataB> list2 = new ArrayList<>();


    public static final String path="gs://myapplication-84520.appspot.com/data/";

    public static ArrayList<Data> getDataList(String str) {
        ArrayList<Data> xktList=new ArrayList<Data>();
        String strs[]=str.split("←");
        for(int i=0;i<strs.length;i++){
            Data xktbean=new Data();
            xktbean.id=Integer.parseInt(strs[i++]);
            xktbean.band=strs[i++];
            xktbean.version=strs[i++];
            xktbean.type=strs[i++];
            xktbean.no=strs[i++];
            xktbean.mp3=strs[i++];
            xktbean.start=strs[i++];
            xktbean.end=strs[i++];
            xktbean.question=strs[i++];
            xktbean.a=strs[i++];
            xktbean.b=strs[i++];
            xktbean.c=strs[i++];
            xktbean.d=strs[i++];
            xktbean.answer=strs[i++];
            xktbean.score=strs[i];
            xktList.add(xktbean);
        }
        return xktList;
    }

    public static String timeConversion(int time) {
        int hour = 0;
        int minutes = 0;
        int sencond = 0;
        int temp = time % 3600;
        if (time > 3600) {
            hour = time / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    minutes = temp / 60;
                    if (temp % 60 != 0) {
                        sencond = temp % 60;
                    }
                } else {
                    sencond = temp;
                }
            }
        } else {
            minutes = time / 60;
            if (time % 60 != 0) {
                sencond = time % 60;
            }
        }
        return (hour < 10 ? ("0" + hour) : hour) + ":" + (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (sencond < 10 ? ("0" + sencond) : sencond);
    }


}
