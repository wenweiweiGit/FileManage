package cn.intsmaze.file.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;

import cn.intsmaze.file.entity.UserFile;

/**
 * 转换类
 */
public class UserFileUtils {

	 /**
	 * @Name: userFileToDocument
	 * @Description:将文件信息转换为document，其中只转换文件信息的fileId（不分词只索引和存储），date（不分词和索引，只存储），title和introduce（分词，索引，存储）
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2015-10-23（创建日期）
	 * @Parameters: 无
	 */
	public static Document userFileToDocument(UserFile userFile){
		
		Document document=new Document();	
		
		//不分词，索引，只存储,(比如网页的url，它不需要搜索和分词，只需要存储在document中，能够读取出该值即可)
		Field fileId=new StringField("fileId", userFile.getFileId(), Store.YES);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		Field date=new StoredField("date", df.format(userFile.getDate()));
					
		//分词，索引，存储
		Field title=new TextField("title", userFile.getTitle(), Store.YES);
		Field introduce=new TextField("introduce",userFile.getIntroduce(),Store.YES);
			
		//把域添加到Document中
		document.add(fileId);
		document.add(date);
		document.add(title);
		document.add(introduce);	
		return document;		
	}
	
	 /**
	 * @Name: documentToUserFile
	 * @Description:将document转换为UserFile，然后显示在页面
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2015-10-23（创建日期）
	 * @Parameters: 无
	 */
	public static UserFile documentToUserFile(Document document) throws ParseException{
		
		UserFile userFile = new UserFile();
		
		userFile.setFileId(document.get("fileId"));
		Date  date  =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(document.get("date"));     
		userFile.setDate(date);
		userFile.setTitle(document.get("title"));
		userFile.setIntroduce(document.get("introduce"));
		
		return userFile;	
	}
	
}
