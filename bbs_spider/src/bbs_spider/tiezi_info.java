package bbs_spider;

public class tiezi_info{
	
	public String title,author,date,link;
	
	public tiezi_info(String t,String a,String d,String l)
	{
		this.title=t;
		this.author=a;
		this.date=d;
		this.link=l;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
	
	public String getDate()
	{
		return this.date;
	}
	
	public String getLink()
	{
		return this.link;
	}
	
	public void Details()
	{
		System.out.println(this.author+'\t'+this.date+'\t'+this.title+'\t'+this.link);
	}
}
