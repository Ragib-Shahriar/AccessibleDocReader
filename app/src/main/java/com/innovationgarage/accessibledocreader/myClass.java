package com.innovationgarage.accessibledocreader;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by Ragib on 5/1/2018.
 * this page contains raw code to read files and sort the file's content
 * i will not comment all the code, because even if the codes are fairly simple,
 * the codes are messy.
 */

public class myClass {

    static List<String> tags=new ArrayList<String>();
    static String code="12usxakkalpp";
    public static String content;

   public static void tagINIT(){
        tags.add("w:p");tags.add("/w:p");tags.add("w:r");tags.add("w:t");tags.add("w:lastRenderedPageBreak/");tags.add("w:pict");
        tags.add("w:pgSz");tags.add("w:tbl");tags.add("/w:tbl");tags.add("w:tr");tags.add("/w:tr");tags.add("w:tc");tags.add("/w:tc");
    }

    public static List<String> separateByLineForTextFile(List list) {
        List<String> lsSt=new ArrayList<String>();
        StringBuffer sb=new StringBuffer();
        sb.append("");

        System.out.println(list.toString());

        for(int i=0;i<list.size();i++) {
            int j=0;int mem=0;
            String st=list.get(i).toString();

            while(j<st.length()) {
                if(st.charAt(j)=='.' || st.charAt(j)=='।' ||  st.charAt(j)=='?' || st.charAt(j)=='!') {
//					if(!sb.toString().equals("")) {
//						String s;
//						if(sb.toString().charAt(0)==' ' && sb.toString().length()>0) {
//							s=sb.toString().substring(1,sb.toString().length());
//							sb.delete(0,sb.toString().length());
//							sb.append(s);
//						}
//					}

                    lsSt.add(sb.toString()+st.substring(mem, j+1)+" ");
                    sb.delete(0,sb.toString().length());
                    sb.append("");
                    mem=j+1;
                }
                else if(!(st.charAt(j)=='.' || st.charAt(j)=='।' ||  st.charAt(j)=='?' || st.charAt(j)=='!') && j==st.length()-1) {
                    sb.append(st.substring(mem, j+1));
                    sb.append(" ");
                    mem=j+1;
                }
                j++;
            }


            if(i==list.size()-1) {
                if(!st.isEmpty()) {
                    if(!(st.charAt(j-1)=='.' || st.charAt(j-1)=='।' ||  st.charAt(j-1)=='?' || st.charAt(j-1)=='!'))
                    {
                        System.out.println(sb.toString());
                        lsSt.add(sb.toString()+st.substring(mem, j)+" ");
                    }
                }
                else {
                    lsSt.add(sb.toString()+" ");
                }
            }


        }
        return lsSt;
    }

    public static List<String> separateByParagraph(List list) {
        //System.out.println(list.toString());
        List<String> temp=new ArrayList<String>();

        int i=0;
        while(i<list.size()) {
 //           if(!list.get(i).equals("")) {
                temp.add(list.get(i).toString());
 //           }
            i++;
        }
        if(temp.isEmpty())temp.add("");
        return temp;
    }


    public static List<List> separateByPageForTextFile(String content) {
        List<List> lst=new ArrayList<List>();
        List<String> lsSt=new ArrayList<String>();

        int mem=0;
        int j=0;
        int i=0;
        for(;i<content.length();i++) {

            if(content.charAt(i)=='\n' || i==content.length()-1) {

                if(content.charAt(i)!='\n') {
                    lsSt.add(content.substring(mem,i+1));
                }
                else if(mem!=i) {

                    j++;

                    lsSt.add(content.substring(mem,i-1));

                    i++;
                    mem=i;
                    if(j==10) {
                        j=0;
                        lst.add(new ArrayList<>(lsSt));
                        lsSt.clear();
                    }

                }

            }
        }

        if((j<10)) {
            lst.add(new ArrayList<>(lsSt));
            lsSt.clear();
        }
        if(lst.isEmpty()){
            lsSt.clear();
            lsSt.add("");
            lst.add(new ArrayList<>(lsSt));
        }
        return lst;
    }


    public static List<String> separateByWord(String str) {
        List<String> lsSt=new ArrayList<String>();


        int j=0,mem=0;
        String st=str;
        while(j<st.length()) {
//            if(j==0 && st.charAt(j)==' ') {
//                while(j<st.length() && st.charAt(j)!=' ')
//                    j++;
//
//                mem=j;
//            }

            if(st.charAt(j)==' ') {
                if(!st.substring(mem,j).equals(",") && mem!=j)
                    lsSt.add(st.substring(mem,j));

//                while(j<st.length() && st.charAt(j)==' ') {
//                    j++;
//                }
                mem=j;
            }
            j++;
            if(j==st.length()) {
                lsSt.add(st.substring(mem,j));
            }
        }

        if(lsSt.isEmpty())lsSt.add("");
        return lsSt;
    }

    public static List<String> separateByLetter(String str) {
        List<String> lsSt=new ArrayList<String>();

        int j=0;
        String st=str;
        while(j<st.length()) {
            //if(st.charAt(j)!=' ') {
                lsSt.add(String.valueOf(st.charAt(j)));
            //}
            j++;
        }

        if(lsSt.isEmpty())lsSt.add("");
        return lsSt;
    }




    public static String paraViewPage(List list) {
        List<String> lsSt=new ArrayList<String>();
        StringBuilder sb=new StringBuilder();

        for(int i=0;i<list.size();i++) {
            //lsSt.add(list.get(i).toString());
            sb.append(list.get(i).toString());
            sb.append("\n");
        }

        return sb.toString();
    }


    public static List<String> separateByLine(String string){
//        List<String> lsSt=new ArrayList<String>();
//
//        if(MainActivity.fileType==0){
//            lsSt=separateByLineForDocxFile(list);
//        }
//        else if(MainActivity.fileType==1){
//            lsSt=separateByLineForTextFile(list);
//        }
        return separateByLineForDocxFile(string);
    }

    public static List<String> separateByLineForDocxFile(String string) {
        List<String> lsSt=new ArrayList<String>();
        int j=0;int mem=0;
        while(j<string.length()) {
            if(string.charAt(j)=='.' || string.charAt(j)=='।' ||  string.charAt(j)=='?' || string.charAt(j)=='!') {
                lsSt.add(string.substring(mem,j+1));
                mem=j+1;
            }
            else if(j==string.length()-1) {
                lsSt.add(string.substring(mem,j+1));
            }
            j++;
        }
        if(lsSt.isEmpty())lsSt.add("");
        return lsSt;
    }


    public static List<List> separateByPage(List<String> listContent) {
        List<List> ls=new ArrayList<List>();
        List<String> lsSt=new ArrayList<String>();

        int i=0;

        if(!listContent.isEmpty()) {

            while(i<listContent.size()) {
                if(listContent.get(i).equals(code+"NEW PAGE") || i==listContent.size()-1) {
                    List<String> temp=new ArrayList<String>();
                    temp=lsSt;
                    ls.add(new ArrayList<String>(temp));
                    lsSt.clear();
                }
                else {
                    lsSt.add(listContent.get(i));
                }
                i++;
            }

        }
        if(lsSt.isEmpty()){
            lsSt.clear();
            lsSt.add("");
            ls.add(new ArrayList<String>(lsSt));
        }
        return ls;
    }


    public static List<String> decodeXML(String c) {
        content=c;
        List<String> lst=new ArrayList<String>();
        int ind=0;
        while(ind<content.length()) {
            Log.d("-------------------","inside DecodeXML");
            List<String> s=new ArrayList<String>();
            s=findNextTag(ind);
            if(!s.isEmpty()) {
                ind=Integer.parseInt(s.get(0));


                if(s.get(1).equals("w:pgSz")) {
                    s=findNextAttr("w:w",ind);
                    if(!s.isEmpty()) {
                        ind=Integer.parseInt(s.get(0));
                        if(s.get(1).equals("16839")) {
                            lst.add(0,"A3 paper");
                        }
                        else if(s.get(1).equals("11907")) {
                            lst.add(0,"A4 paper");
                        }
                        else if(s.get(1).equals("8391")) {
                            lst.add(0,"A5 paper");
                        }
                        else if(s.get(1).equals("12240")) {
                            lst.add(0,"Letter");
                        }
                        else {
                            lst.add(0,"Page size not recognized");
                        }

                    }
                }

                if(s.get(1).equals("w:tbl")) {
                    List<String> temp=new ArrayList<String>();
                    temp=handleTable(ind);
                    if(!temp.isEmpty()) {
                        ind=Integer.parseInt(temp.get(0));
                        for(int i=1;i<temp.size();i++) {
                            lst.add(temp.get(i));
                        }
                    }
                }

//				if(s.get(1).equals("w:lastRenderedPageBreak/")) {
//					lst.add("-------------------NEW PAGE-------------------");
//				}
//
//				if(s.get(1).equals("w:pict")) {
//					lst.add("-------------------HERE IS A PICTURE-------------------");
//				}

//				if(s.get(1).equals("/w:p")) {
//
//				}

                if(s.get(1).equals("w:p")) {
                    StringBuilder sb=new StringBuilder();
                    while(ind<content.length()) {
                        s=findNextTag(ind);
                        if(!s.isEmpty()) {
                            ind=Integer.parseInt(s.get(0));
                            if(s.get(1).equals("/w:p")) {
                                lst.add(sb.toString());
                                break;
                            }

                            if(s.get(1).equals("w:pgSz")) {
                                s=findNextAttr("w:w",ind);
                                if(!s.isEmpty()) {
                                    ind=Integer.parseInt(s.get(0));
                                    if(s.get(1).equals("16839")) {
                                        lst.add(0,"A3 paper");
                                    }
                                    else if(s.get(1).equals("11907")) {
                                        lst.add(0,"A4 paper");
                                    }
                                    else if(s.get(1).equals("8391")) {
                                        lst.add(0,"A5 paper");
                                    }
                                    else if(s.get(1).equals("12240")) {
                                        lst.add(0,"Letter");
                                    }
                                    else {
                                        lst.add(0,"Page size not recognized");
                                    }

                                }
                            }

                            if(s.get(1).equals("w:t")) {
                                while(content.charAt(ind)!='>') {
                                    ind++;
                                }
                                ind++;
                                Integer mem=ind;
                                while(content.charAt(ind)!='<') {
                                    ind++;
                                }
                                sb.append(content.substring(mem, ind));
                            }

                            if(s.get(1).equals("w:lastRenderedPageBreak/")) {
                                lst.add(code+"NEW PAGE");
                            }

                            if(s.get(1).equals("w:pict")) {
                                lst.add("HERE IS A PICTURE ");
                            }
                        }
                        ind++;
                    }
                }

//				if(s.get(1).equals("w:t")) {
//					while(content.charAt(ind)!='>') {
//						ind++;
//					}
//					ind++;
//					Integer mem=ind;
//					while(content.charAt(ind)!='<') {
//						ind++;
//					}
//					lst.add(content.substring(mem, ind));
//				}
            }
            ind++;
        }
        return lst;
    }

    public static List<String> handleTable(Integer ind) {
        List<String> n=new ArrayList<String>();
        int numOfRows=0,numOfColumn=0;
        while(ind<content.length()) {
            List<String> s=new ArrayList<String>();
            s=findNextTag(ind);
            if(!s.isEmpty()) {
                ind=Integer.parseInt(s.get(0));

                if(s.get(1).equals("/w:tbl"))break;

                if(s.get(1).equals("w:tr")) {
                    numOfRows++;
                    numOfColumn=0;
                    StringBuilder sbr=new StringBuilder();
                    sbr.append("The "+numOfRows+" number row ");
                    while(ind<content.length()) {
                        s=findNextTag(ind);

                        if(!s.isEmpty()) {

                            ind=Integer.parseInt(s.get(0));
                            if(s.get(1).equals("/w:tr"))break;

                            if(s.get(1).equals("w:tc")) {
                                numOfColumn++;
                                StringBuilder sbc=new StringBuilder();
                                sbc.append("The "+numOfColumn+" number Column ");
                                while(ind<content.length()) {
                                    s=findNextTag(ind);
                                    if(!s.isEmpty()) {
                                        ind=Integer.parseInt(s.get(0));

                                        if(s.get(1).equals("/w:tc"))break;

                                        if(s.get(1).equals("w:t")) {
                                            while(content.charAt(ind)!='>') {
                                                ind++;
                                            }
                                            ind++;
                                            Integer mem=ind;
                                            while(content.charAt(ind)!='<') {
                                                ind++;
                                            }
                                            sbc.append(content.substring(mem, ind));
                                        }
                                    }
                                    ind++;
                                }
                                sbr.append(sbc.toString());
                                n.add(sbr.toString());
                                sbr.delete(0,sbr.length());
                            }
                        }
                        ind++;
                    }
                }
            }
            ind++;
        }

        n.add(0,ind.toString());
        String st="the number of rows are "+numOfRows+" And the number columns are "+numOfColumn;
        n.add(1,st);
        n.add(1,"HERE IS A TABLE");
        n.add("THE TABLE is FINISHED");
        return n;
    }

    public static List<String> findNextTag(Integer ind) {
        String tg="";Integer mem;
        List<String> s=new ArrayList<String>();
        while(ind<content.length()) {
            if(content.charAt(ind)=='<') {
                mem=ind;
                while(ind<content.length()) {
                    if(content.charAt(ind)==' ' || content.charAt(ind)=='>') break;
                    ind++;
                }
                if(ind<content.length())
                 tg=content.substring(mem+1, ind);


                if(tags.contains(tg)) {
                    s.add(ind.toString());
                    s.add(tg);
                    return s;
                }
            }
            ind++;
        }
        return s;
    }

    public static List<String> findNextAttr(String attr, Integer ind) {
        String atr="";Integer mem;
        while(ind<content.length()) {
            mem=ind;
            while(true) {
                if(content.charAt(ind)=='=' || content.charAt(ind)=='>') break;
                ind++;
            }
            atr=content.substring(mem+1, ind);


            if(atr.equals(attr)) {
                Integer mem1=ind;
                int quoteCount=0;
                while(true) {
                    if(quoteCount==2) break;
                    if(content.charAt(ind)=='"')quoteCount++;
                    ind++;
                }
                List<String> s=new ArrayList<String>();
                s.add(ind.toString());
                s.add(content.substring(mem1+2,ind-1));
                return s;
            }
            if(content.charAt(ind)=='>')break;

            while(ind<content.length() && content.charAt(ind)!=' ' && content.charAt(ind)!='>')
                ind++;

        }
        List<String> s=new ArrayList<String>();
        return s;
    }

    public static String readFromFile(String filePath) {
        String file="null";
        try {
            InputStream in;
            in = new FileInputStream(filePath);
            BufferedReader bfr=new BufferedReader(new InputStreamReader(in));
            StringBuilder sb=new StringBuilder();
            int i=0;
            while(true) {
                i=bfr.read();
                if(i==-1)break;
                sb.append((char)i);
            }
            file=sb.toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void writeToFile(String filePath,String content) {
        try {
            OutputStream os=new FileOutputStream(filePath);
            os.write(content.getBytes());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void print(String print) {
        System.out.println(print);
    }

    public static String getZIPXML(String filePath) {
        String docContent="_couldn't load";
        try {
            ZipFile zip=new ZipFile(new java.io.File(filePath));
            Enumeration entries=zip.entries();
            while(entries.hasMoreElements()) {
                ZipEntry ent=(java.util.zip.ZipEntry) entries.nextElement();
                if(!ent.getName().equals("word/document.xml")) continue;

                InputStream in=zip.getInputStream(ent);
                Log.d("--------","zip File achived");

                docContent=readFromIN(in);
                Log.d("--------","Input stream read successful");
                // 	System.out.println(docContent);
                break;
            }
            zip.close();

        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return docContent;
    }

    public static String readFromIN(InputStream in) {
        String file="null";
        try {
            BufferedReader bfr=new BufferedReader(new InputStreamReader(in));
            StringBuilder sb=new StringBuilder();
            int i=0;
            while(true) {
                i=bfr.read();
                if(i==-1)break;
                sb.append((char)i);
            }
            file=sb.toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return file;
    }

}
