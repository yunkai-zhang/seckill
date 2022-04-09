package com.zhangyun.zseckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.vo.RespBean;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 生成用户工具类
 * @ClassName: UserUtil
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        //生成指定数目的用户。用户的id和用户名不一样
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setLoginCount(1);
            user.setNickname("user" + i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            users.add(user);
        }
        System.out.println("create user");
        //插入数据库
        //使用自定义的方法获取mysql连接
        Connection conn = getConn();
        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("insert to db");

        //登录，生成token
        String urlString = "http://localhost:8080/login/doLogin";
        //把（userid,userticket）写入下面的文件中；
        File file = new File("D:\\CodeProjects\\GitHub\\zseckill\\generatedFiles\\config.txt");
        //如果文件存在的话先删掉
        if (file.exists()) {
            file.delete();
        }
        //新建变量file指定的文件
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            //请求url接口
            //请求需要的入参
            String params = "mobile=" + user.getId() + "&password=" +
                    MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            //请求。请求完了之后有流直接读。
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            //输入完之后，就把输入和输出流关闭
            inputStream.close();
            bout.close();
            //流可以读到响应的结果
            String response = new String(bout.toByteArray());
            //把拿到的String类型的respbean转换成respBean对象
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            //根据respBean拿到userTicket
            String userTicket = ((String) respBean.getObj());
            //打印谁拿到什么userticket
            System.out.println("create userTicket : " + user.getId());
            //一行的数据放到row中
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());//换行
            System.out.println("write to file : " + user.getId());
        }
        //所有用户发起请求url后得到的userTicket写入config.txt后就完事了，可以关闭raf
        raf.close();
        System.out.println("over");
    }
    private static Connection getConn() throws Exception {
        //这个url和下面的一些配置可以从springboot的yaml中拷贝
        String url = "jdbc:mysql://192.168.187.128:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "zhangyun";
        String password = "1234";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
