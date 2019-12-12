package com.cqq.stock.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MySpider {

    private String url;
    private String cookies;
    private String params;
    private String fileURL;
    private String proxyIp;
    private int proxyPort = 80;
    private String formData = null;
    private boolean useCache = false;
    private boolean method = false;
    private String encodingType = null;


    public String getSavePath() {
        return savePath;
    }


    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }


    public String getEncodingType() {
        return encodingType;
    }


    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }


    public boolean isMethod() {
        return method;
    }


    public void setPost(boolean method) {
        this.method = method;
    }


    public String getFormData() {
        return formData;
    }


    public void setFormData(String formData) {
        this.formData = formData;
    }


    public String getProxyIp() {
        return proxyIp;
    }


    public boolean isUseCache() {
        return useCache;
    }


    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }


    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }


    public int getProxyPort() {
        return proxyPort;
    }


    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }


    public String getFileURL() {
        return fileURL;
    }


    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    HashMap<String, String> map = new HashMap<>();
    private String savePath;

    public void put(String key, String value) {
        File file = new File(savePath);
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter(file);
            HashMap<String, String> map = getConfigHashMap();
            map.put(key, value);
            Set<String> set = map.keySet();
            for (String k : set) {
                fw.write(k + "=" + map.get(k) + "\r\n");
            }
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private HashMap<String, String> getConfigHashMap() {

        File file = new File(savePath);
        HashMap<String, String> all = new HashMap<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String str = null;
            while (true) {
                str = br.readLine();
                if (str == null) {
                    break;
                }
                String k_v[] = str.split("=");
                all.put(k_v[0], k_v[1]);
            }
            return all;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new HashMap<>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    public String getString(String key) {
        File file = new File(savePath);
        if (file.exists() == false) {
            return null;
        }
        return getConfigHashMap().get(key);


    }

    public Integer getInteger(String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (Exception e) {
            return 0;
        }
    }


    public MySpider() {
        super();
        if (encodingType == null) {
            encodingType = "UTF-8";
        }
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getCookies() {
        return cookies;
    }


    public void setCookies(String cookies) {
        this.cookies = cookies;
    }


    public String getParams() {
        return params;
    }

    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public void sleep(int min, int max) {
        if (min > max) {
            int t = min;
            min = max;
            max = t;
        }
        int a = (int) (Math.random() * (max - min));
        try {
            Thread.sleep(a + min);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void setParams(String params) {
        this.params = params;
    }

    public List<String> matcher(String source, String regex) {

        return matcher(source, regex, false);
    }

    public static List<String> match(String source, String pattern, int group) {
        return match(source, pattern, group, false);
    }

    public static List<String> match(List<String> sources, String pattern, int group, boolean show) {
        List<String> list = new ArrayList<String>();
        for (String source : sources) {
            list.addAll(match(source, pattern, group, show));
        }


        return list;
    }

    public static List<String> match(String source, String pattern, int group, boolean show) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(source);
        List<String> list = new ArrayList<String>();
        while (m.find()) {
            String url = m.group(group);
            // 	 System.out.println(url);
            list.add(url);

        }
        if (show) {
            for (String s : list) {
                System.out.println(s);
            }
            System.out.println("size:" + list.size());
        }


        return list;
    }

    public List<String> matcher(String source, String regex, int group) {

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(source);

        List<String> list = new ArrayList<String>();

        while (matcher.find()) {

            list.add(matcher.group(group));
        }

        return list;
    }

    public List<String> matcher(List<String> list, String regex, int group) {
        List<String> a = new ArrayList<String>();

        int sum = 0;
        for (String str : list) {
            List<String> temp = matcher(str, regex, group);
            sum += temp.size();
            a.addAll(temp);

        }
        return a;
    }

    public List<String> matcher(String source, String regex, boolean show) {

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(source);

        List<String> list = new ArrayList<String>();

        while (matcher.find()) {

            list.add(matcher.group());
        }

        if (show) {

            for (String s : list) {
                System.out.println(s);

            }
            System.out.println(list.size());
        }

        return list;


    }

    public List<String> matcher(List<String> list, String regex, boolean show) {
        List<String> a = new ArrayList<String>();

        int sum = 0;
        for (String str : list) {
            List<String> temp = matcher(str, regex, false);
            sum += temp.size();
            if (show) {
                for (String s : temp) {
                    System.out.println(s);
                }
            }
            a.addAll(temp);

        }
        if (show) System.out.println(sum);
        return a;

    }

    public List<String> matcher(List<String> list, String regex) {
        return matcher(list, regex, false);

    }

    public String doing(String url) {
        setUrl(url);
        return doing();
    }

    public String doing() {
        try {
            return getResponseBody();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    public String getResponseBody() throws Exception {

        if (useCache) {
            if (fileURL != null && !fileURL.equals("")) {
                File file = new File(fileURL);

                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedReader br = new BufferedReader(new FileReader(file));
                String info = "";
                String str = null;
                while ((str = br.readLine()) != null) {
                    info += str;
                }
                br.close();
                if (info.length() != 0) {
                    return info;
                }


            }


        }

        URL uri = new URL(url);

        URLConnection conn = null;

        if (proxyIp == null || proxyIp.equals("")) {

            conn = (URLConnection) uri.openConnection();

        } else {

            InetSocketAddress addr = new InetSocketAddress(proxyIp, proxyPort);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理


            conn = uri.openConnection(proxy);


        }


        if (formData != null && !formData.equals("")) {
            conn.setRequestProperty("Content-Type", "multipart/form-data; " + formData);
        }


        //

        conn.setConnectTimeout(15000);

        conn.setReadTimeout(15000);
        conn.setConnectTimeout(1000);

        conn.setRequestProperty("connection", "keep-alive");

        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)");

        if (cookies != null && !cookies.equals("")) conn.setRequestProperty("Cookie", cookies);


        conn.setDoInput(true);

        conn.setDoOutput(method);

        conn.connect();


        if (params != null && !params.equals("")) {
            OutputStreamWriter out = new OutputStreamWriter(
                    conn.getOutputStream());

            out.write(params);
            out.flush();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), encodingType));
        String lines;
        String response = "";
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "UTF-8");
            response += lines;
        }
        reader.close();

        //    conn.disconnect();  // 断开连接

        if (fileURL != null && !fileURL.equals("")) {
            File f = new File(fileURL);
            FileWriter fw = new FileWriter(f);
            fw.append(response);
            fw.close();

        }
        return response;

    }

    public boolean check(String ip, int port) {
        URL url = null;
        try {
            url = new URL("http://www.baidu.com");
        } catch (MalformedURLException e) {
            System.out.println("url invalidate");
        }
        InetSocketAddress addr = null;
        addr = new InetSocketAddress(ip, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http proxy
        InputStream in = null;
        try {
            URLConnection conn = url.openConnection(proxy);
            conn.setConnectTimeout(1000);
            in = conn.getInputStream();
        } catch (Exception e) {
            System.out.println(ip + " is not aviable");//异常IP
            return false;

        }
        String s = convertStreamToString(in);

        if (s.indexOf("baidu") > 0) {//有效IP
            System.out.println(ip + ":" + port + " is ok");
            return true;
        }
        return false;
    }

    private String convertStreamToString(InputStream is) {
        if (is == null)
            return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }

    public List<String> cutDown(List<String> info, boolean show, String... cuts) {
        List<String> all = new ArrayList<String>();
        for (String s : info) {
            for (String cut : cuts) {
                s = s.replace(cut, "");
            }
            all.add(s);
        }
        if (show) {
            for (String a : all) {
                System.out.println(a);
            }
        }
        if (show) System.out.println(all.size());

        return all;


    }

    public void reDownload(String src, String path) {
        int cnt = 0;
        boolean success = download(src, path);
        while (success == false && cnt < 4) {
            success = download(src, path);
            cnt++;
        }


    }

    public static void main(String[] args) {

    }

    public boolean download(String src, String path) {

        File file = new File(path);

        File father = file.getParentFile();

        if (father.exists() == false) {

            father.mkdirs();
        }

        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }


        List<String> listImgSrc = new ArrayList<String>();
        listImgSrc.add(src);
        try {
            //开始时间
            Date begindate = new Date();
            System.out.println(src);
            URL uri = new URL(src);

            URLConnection conn = null;

            if (proxyIp == null || proxyIp.equals("")) {

                conn = (URLConnection) uri.openConnection();

            } else {

                InetSocketAddress addr = new InetSocketAddress(proxyIp, proxyPort);

                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理


                conn = uri.openConnection(proxy);

            }
            for (String url : listImgSrc) {
                //开始时间
                long begin = System.currentTimeMillis();
                String imageName = url.substring(url.lastIndexOf("/") + 1, url.length());
                conn.getInputStream();
                InputStream in = conn.getInputStream();
                FileOutputStream fo = new FileOutputStream(file);//文件输出流
                byte[] buf = new byte[1024];
                int length = 0;
                System.out.println("开始下载:" + url);
                while ((length = in.read(buf, 0, buf.length)) != -1) {
                    fo.write(buf, 0, length);
                }
                fo.flush();
                //关闭流
                in.close();
                fo.close();
                System.out.println("${path}下载完成,耗时${time}".replace("${path}", path).replace("${time}", ((System.currentTimeMillis() - begin) / 1000.0) + "秒"));

            }
        } catch (Exception e) {
            System.out.println("下载失败");
            e.printStackTrace();
            return false;
        }
        return true;
    }


}