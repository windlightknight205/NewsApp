package com.example.hp.appdocbao;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Customadapter customadapter;
    ArrayList<Docbao> mangdocbao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listview);
        mangdocbao= new ArrayList<Docbao>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Readdata().execute("https://vnexpress.net/rss/the-gioi.rss");
            }
        });
    }
    class Readdata extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            return ReadContent(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            XMLDOMParser parser= new XMLDOMParser();
            Document document= parser.getDocument(s);
            NodeList nodeList= document.getElementsByTagName("item");
            NodeList nodeListdesciption=document.getElementsByTagName("description");
            String hinhanh="";
            String title= "";
            String link="";
            for(int i=0;i<nodeList.getLength();i++) {
                String cdata= nodeListdesciption.item(i+1).getTextContent();
                Pattern p =Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                Matcher m = p.matcher(cdata);
                if(m.find()){
                    hinhanh=m.group(1);
                }
                Element element = (Element) nodeList.item(i);
                title = parser.getValue(element, "title");
                link= parser.getValue(element,"link");
                mangdocbao.add(new Docbao(title,link,hinhanh));
            }
            customadapter= new Customadapter(MainActivity.this,android.R.layout.simple_list_item_1,mangdocbao);
            listView.setAdapter(customadapter);
            super.onPostExecute(s);
        }
    }
    private String ReadContent(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {

            URL url = new URL(theUrl);

            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
