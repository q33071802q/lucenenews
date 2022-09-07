package com.chaoxing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * lucene工具类
 * @author Administrator
 *
 */
public class LuceneUtil {

    /**
     * 获取索引文件存放的文件夹对象
     * 
     * @param path
     * @return
     */
    public static Directory getDirectory(String path) {
        Directory directory = null;
        try {
            directory = FSDirectory.open(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directory;
    }

    /**
     * 索引文件存放在内存
     * 
     * @return
     */
    public static Directory getRAMDirectory() {
        Directory directory = new RAMDirectory();
        return directory;
    }

    /**
     * 文件夹读取对象
     * 
     * @param directory
     * @return
     */
    public static DirectoryReader getDirectoryReader(Directory directory) {
        DirectoryReader reader = null;
        try {
            reader = DirectoryReader.open(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    /**
     * 文件索引对象
     * 
     * @param reader
     * @return
     */
    public static IndexSearcher getIndexSearcher(DirectoryReader reader) {
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        return indexSearcher;
    }

    /**
     * 写入索引对象
     * 
     * @param directory
     * @param analyzer
     * @return
     */
    public static IndexWriter getIndexWriter(Directory directory, Analyzer analyzer)

    {
        IndexWriter iwriter = null;
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(OpenMode.CREATE_OR_APPEND);
            // Sort sort=new Sort(new SortField("content", Type.STRING));
            // config.setIndexSort(sort);//排序
            config.setCommitOnClose(true);
            // 自动提交
            // config.setMergeScheduler(new ConcurrentMergeScheduler());
            // config.setIndexDeletionPolicy(new
            // SnapshotDeletionPolicy(NoDeletionPolicy.INSTANCE));
            iwriter = new IndexWriter(directory, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iwriter;
    }

    /**
     * 关闭索引文件生成对象以及文件夹对象
     * 
     * @param indexWriter
     * @param directory
     */
    public static void close(IndexWriter indexWriter, Directory directory) {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                indexWriter = null;
            }
        }
        if (directory != null) {
            try {
                directory.close();
            } catch (IOException e) {
                directory = null;
            }
        }
    }

    /**
     * 关闭索引文件读取对象以及文件夹对象
     * 
     * @param reader
     * @param directory
     */
    public static void close(DirectoryReader reader, Directory directory) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                reader = null;
            }
        }
        if (directory != null) {
            try {
                directory.close();
            } catch (IOException e) {
                directory = null;
            }
        }

    }

    /**
     * 高亮标签
     * 
     * @param query
     * @param fieldName
     * @return
     */

    public static Highlighter getHighlighter(Query query, String fieldName)

    {
        Formatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Scorer fragmentScorer = new QueryTermScorer(query, fieldName);
        Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
        highlighter.setTextFragmenter(new SimpleFragmenter(200));
        return highlighter;
    }
}