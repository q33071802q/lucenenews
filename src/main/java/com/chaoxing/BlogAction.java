package com.chaoxing;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.store.Directory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogAction {
    private String title;
    private BlogDao blogDao = new BlogDao();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String list() {
        try {
            HttpServletRequest request = ServletActionContext.getRequest();
            if (StringUtils.isBlank(title)) {
                List<Map<String, Object>> blogList = this.blogDao.list(title, null);
                request.setAttribute("blogList", blogList);
            }else {
                Directory directory = LuceneUtil.getDirectory(PropertiesUtil.getValue("indexPath"));
                DirectoryReader reader = LuceneUtil.getDirectoryReader(directory);
                IndexSearcher searcher = LuceneUtil.getIndexSearcher(reader);
                SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
//                拿一句话到索引目中的索引文件中的词库进行关键词碰撞
                Query query = new QueryParser("title", analyzer).parse(title);
                Highlighter highlighter = LuceneUtil.getHighlighter(query, "title");
                
                TopDocs topDocs = searcher.search(query , 100);
                //处理得分命中的文档
                List<Map<String, Object>> blogList = new ArrayList<>();
                Map<String, Object> map = null;
                ScoreDoc[] scoreDocs = topDocs.scoreDocs;
                for (ScoreDoc scoreDoc : scoreDocs) {
                    map = new HashMap<>();
                    Document doc = searcher.doc(scoreDoc.doc);
                    map.put("id", doc.get("id"));
                    String titleHighlighter = doc.get("title");
                    if(StringUtils.isNotBlank(titleHighlighter)) {
                        titleHighlighter = highlighter.getBestFragment(analyzer, "title", titleHighlighter);
                    }
                    map.put("title", titleHighlighter);
                    map.put("url", doc.get("url"));
                    blogList.add(map);
                }
                
                request.setAttribute("blogList", blogList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "blogList";
    }
}