package xmlparse.cn.com.myxmlparse;

import android.content.Context;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by chenwenjie on 2015/5/20.
 */
public class XmlParser {

    /**
     * 从asset中获取流
     * @param filename
     * @param context
     * @return
     */
    public InputStream getInputStreamFromAsset(String filename,Context context){
        try {
            return context.getAssets().open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * sax解析
     * @param inputStream
     * @return
     */
    public List<Person> readXmlBySax(InputStream inputStream){
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLContentHandler handler = new XMLContentHandler();
            //设置解析器的相关特性，true表示开启命名空间特性
//            saxParser.setProperty("http://xml.org/sax/features/namespaces",true);
            saxParser.parse(inputStream,handler);

            inputStream.close();

            return handler.getPersons();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * DOM方式解析
     * @param inputStream
     * @return
     */
    public List<Person> readXmlByDom(InputStream inputStream){
        List<Person> persons = new ArrayList<Person>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element root = document.getDocumentElement();
            //查找所有person节点
            NodeList items = root.getElementsByTagName("person");
            for (int i = 0;i < items.getLength();i++){
                Person person = new Person();
                Element personNode = (Element) items.item(i);
                person.setId(Integer.parseInt(personNode.getAttribute("id")));
                NodeList childNodes = personNode.getChildNodes();
                for (int j = 0;j<childNodes.getLength();j++){
                    Node node = childNodes.item(j);
                    //判断是否为元素类型
                    if(node.getNodeType() == Node.ELEMENT_NODE){
                        Element childNode = (Element)node;
                        if("name".equals(childNode.getNodeName())){
                            person.setName(childNode.getFirstChild().getNodeValue());
                        }else if("age".equals(childNode.getNodeName())){
                            person.setAge(Short.parseShort(childNode.getFirstChild().getNodeValue()));
                        }
                    }
                }
                persons.add(person);
            }
            inputStream.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return persons;
    }

    public List<Person> readXmlByPull(InputStream inputStream){
        XmlPullParser parser = Xml.newPullParser();
        List<Person> persons = null;
        Person currentPerson = null;
        try {
            parser.setInput(inputStream,"UTF-8");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        persons = new ArrayList<Person>();
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if(name.equals("person")){
                            currentPerson = new Person();
                            currentPerson.setId(Integer.parseInt(parser.getAttributeValue(null,"id")));
                        }else if(currentPerson!=null){
                            if (name.equals("name")){
                                currentPerson.setName(parser.nextText());
                            }else if(name.equals("age")){
                                currentPerson.setAge(new Short(parser.nextText()));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("person") && currentPerson != null) {
                            persons.add(currentPerson);
                            currentPerson = null;
                        }
                        break;
                }
                eventType = parser.next();
//                if (parser.getName().equalsIgnoreCase("person") && currentPerson != null) {
//                    persons.add(currentPerson);
//                    currentPerson = null;
//                }

            }
            inputStream.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return persons;
    }
    //成XML文件
//使用Pull解析器生成一个与itcast.xml文件内容相同的myitcast.xml文件。
    public static String writeXML(List<Person> persons, Writer writer){

        XmlSerializer serializer = Xml.newSerializer();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            //第一个参数为命名空间,如果不使用命名空间,可以设置为null
            serializer.startTag("", "persons");

            for (Person person : persons){
                serializer.startTag("", "person");
                serializer.attribute("", "id", person.getId()+"");
                serializer.startTag("", "name");
                serializer.text(person.getName());
                serializer.endTag("", "name");
                serializer.startTag("", "age");
                serializer.text(person.getAge()+"");
                serializer.endTag("", "age");
                serializer.endTag("", "person");
            }

            serializer.endTag("", "persons");
            serializer.endDocument();

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
