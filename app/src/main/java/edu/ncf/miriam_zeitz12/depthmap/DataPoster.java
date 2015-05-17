package edu.ncf.miriam_zeitz12.depthmap;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by miriamzeitz on 5/16/15.
 */
public class DataPoster extends AsyncTask<Map<String, Map<String,String>>, Void, String> {


    @Override
    protected String doInBackground(Map<String, Map<String, String>>... params) {
        return null;
    }

    public void postData(String url, Map<String, String> data){

        JSONObject json = new JSONObject(data);
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> postData = new ArrayList<>();
        postData.add(new BasicNameValuePair("data[]", json.toString()));
        HttpClient client = HttpClientBuilder.create().build();
    try {
        StringEntity entity = new StringEntity(json.toString());



        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("charset","utf-8");
        httpPost.setHeader("Content-Length",Integer.toString(json.toString().getBytes("UTF-8").length));
        HttpResponse response = client.execute(httpPost);
        if(response!=null){
            InputStream in = new BufferedInputStream(response.getEntity().getContent()); //Get the data in the entity

        }
    }
    catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        }
    catch (ClientProtocolException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
