package com.eayon.config;

import com.eayon.pojo.Configuration;
import com.eayon.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 使用dom4j解析mapper.xml
 */
public class XMLMapperBuilder {

    //声明核心配置类成员变量
    private Configuration configuration;

    //在使用有参构造new的时候会将传递的configuration进行赋值
    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 该方法使用dom4j将mapper.xml字节流进行解析并封装到MappedStatement配置实体，并将该实体赋值给Configuration中的属性。
     *
     * @param is *mapper.xml的字节流
     */
    public void parse(InputStream is) throws DocumentException {
        //通过dom4j读取mapper.xml字节流生成Dom
        Document document = new SAXReader().read(is);
        //拿到根元素，也就是mapper.xml文件中的mapper标签
        Element mapperElement = document.getRootElement();
        //查找根元素下所有的select标签元素
        List<Element> selectElements = mapperElement.selectNodes("//select");

        //获取每个select操作的SQL配置
        for (Element selectElement : selectElements) {
            String id = selectElement.attributeValue("id");
            String resultType = selectElement.attributeValue("resultType");
            String paramterType = selectElement.attributeValue("paramterType");
            String sql = selectElement.getTextTrim();//sql语句
            //封装到MappedStatement
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParamterType(paramterType);
            mappedStatement.setSql(sql);
            //将每个MappedStatement存储到Configuration的MappedStatementMap属性中
            Map<String, MappedStatement> mappedStatementMap = configuration.getMappedStatementMap();
            //构建mappedStatementMap的key ：statementId（namespace.id），通过statementId可以定位到每一条SQL
            String namespace = mapperElement.attributeValue("namespace");
            String statementId = namespace + "." + id;
            mappedStatementMap.put(statementId, mappedStatement);
        }

    }
}
