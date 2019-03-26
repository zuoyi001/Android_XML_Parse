package xmlparse.cn.com.myxmlparse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwenjie on 2015/5/20.
 */
public class XMLContentHandler extends DefaultHandler {

    private List<Person> persons;
    private Person currentPerson;
    private String tagName;

    public List<Person> getPersons(){
        return persons;
    }

    //接收文档开始的通知,可以在其中做一些预处理的工作。
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        persons = new ArrayList<>();

    }

    //接收元素开始的通知。当读到一个开始标签的时候，会触发这个方法。其中namespaceURI表示元素的命名空间；
    //localName表示元素的本地名称（不带前缀）；qName表示元素的限定名（带前缀）；atts 表示元素的属性集合
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equals("person")){
            currentPerson = new Person();
            currentPerson.setId(Integer.parseInt(attributes.getValue("id")));
        }
        tagName = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (tagName!=null){
            String data = new String(ch,start,length);
            if(tagName.equals("name")){
                currentPerson.setName(data);
            }else if (tagName.equals("age")){
                currentPerson.setAge(Short.parseShort(data));
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equals("person")){
            persons.add(currentPerson);
            currentPerson = null;
        }
        tagName = null;
    }
}
