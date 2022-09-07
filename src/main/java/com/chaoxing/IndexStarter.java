package com.chaoxing;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 构建lucene索引
 * @author Administrator
 * 1。构建索引    IndexWriter
 * 2、读取索引文件，获取命中片段
 * 3、使得命中片段高亮显示
 *
 */
public class IndexStarter {
    private static BlogDao blogDao = new BlogDao();
    public static void main(String[] args) {
        IndexWriterConfig conf = new IndexWriterConfig(new SmartChineseAnalyzer());
        Directory d;
        IndexWriter indexWriter = null;
        try {
            d = FSDirectory.open(Paths.get(PropertiesUtil.getValue("indexPath")));
            indexWriter = new IndexWriter(d , conf );
            
//            为数据库中的所有数据构建索引
            List<Map<String, Object>> list = blogDao.list(null, null);
            for (Map<String, Object> map : list) {
                Document doc = new Document();
                doc.add(new StringField("id", (String) map.get("id"), Field.Store.YES));
//                TextField用于对一句话分词处理    java培训机构
                doc.add(new TextField("title", (String) map.get("title"), Field.Store.YES));
                doc.add(new StringField("url", (String) map.get("url"), Field.Store.YES));
                indexWriter.addDocument(doc);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(indexWriter!= null) {
                    indexWriter.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}