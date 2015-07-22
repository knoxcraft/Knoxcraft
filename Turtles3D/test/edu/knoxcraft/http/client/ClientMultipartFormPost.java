/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package edu.knoxcraft.http.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Example how to use multipart/form encoded POST request.
 */
public class ClientMultipartFormPost {

    private static String readFromFile(File file) throws IOException {
        StringBuilder buf=new StringBuilder();
        Scanner scan=new Scanner(new FileInputStream(file));
        while (scan.hasNextLine()) {
            String line=scan.nextLine();
            buf.append(line);
            buf.append("\n");
        }
        scan.close();
        return buf.toString();
    }
    
    public static void main(String[] args) throws Exception {
        //String url="http://localhost:8888/mcform";
        String url="http://localhost:8888/kctupload";
        String playerName="ppypp-emhastings-masters-of-minecraft-hackery";
        File jsonfile=new File("testdata/testcommands1.json");
        File sourcefile=new File("test/edu/knoxcraft/turtle3d/SampleProgram.java");
        upload(url, playerName, jsonfile, sourcefile);
    }
    public static void upload(String url, String playerName, File jsonfile, File sourcefile) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);

            FileBody json= new FileBody(jsonfile);
            FileBody source= new FileBody(sourcefile);
            String jsontext=readFromFile(jsonfile);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("playerName", new StringBody(playerName, ContentType.TEXT_PLAIN))
                    .addPart("jsonfile", json)
                    .addPart("sourcefile", source)
                    .addPart("language", new StringBody("java", ContentType.TEXT_PLAIN))
                    .addPart("jsontext", new StringBody(jsontext, ContentType.TEXT_PLAIN))
                    .addPart("sourcetext", new StringBody("public class Foo {\n  int x=5\n}", ContentType.TEXT_PLAIN))
                    .build();


            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                //System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    //System.out.println("Response content length: " + resEntity.getContentLength());
                    Scanner sc=new Scanner(resEntity.getContent());
                    while (sc.hasNext()) {
                        System.out.println(sc.nextLine());
                    }
                    sc.close();
                    EntityUtils.consume(resEntity);
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}
