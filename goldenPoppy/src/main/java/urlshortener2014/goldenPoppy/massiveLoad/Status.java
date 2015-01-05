package urlshortener2014.goldenPoppy.massiveLoad;

public class Status {

	private double percent;
	private String status;
	
	public Status(double p, String s){
		this.percent = p;
		this.status = s;
	}
	
	public double getPercent(){
		return this.percent;
	}
	
	public String getStatus(){
		return this.status;
	}
}
