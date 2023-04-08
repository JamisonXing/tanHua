package com.itheima.test;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class FaceTest {
    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Test
    public void detect() {
        boolean detect = aipFaceTemplate.detect("https://tanhua-jamison.oss-cn-hangzhou.aliyuncs.com/2023/04/07/f2420997-6e90-45b8-862d-82d3c8bc5295.png");
        System.out.println(detect);
    }
    //设置APPID/AK/SK
    public static final String APP_ID = "32114926";
    public static final String API_KEY = "Pns9SUFAt1HrWhyGGPKgjOPs";
    public static final String SECRET_KEY = "eD0jgzjPGrrmFGzY8dHEOHxCqxcWfGnn";

    public static void main(String[] args) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);


        // 调用接口
        String image = "https://tanhua-jamison.oss-cn-hangzhou.aliyuncs.com/2023/04/07/7bfe5fb3-d74d-4f9c-bf6f-beb9b914f597.png";
        String imageType = "URL";

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString(2));

    }

}
