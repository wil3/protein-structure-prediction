package edu.stevens.cs.ssp;


public class ThreadResponse {
	private double error;
	private double q3;
	
	public ThreadResponse(double error, double q3){
		this.error = error;
		this.q3 = q3;
	}
	/**
	 * @return the error
	 */
	public double getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(double error) {
		this.error = error;
	}
	/**
	 * @return the q3
	 */
	public double getQ3() {
		return q3;
	}
	/**
	 * @param q3 the q3 to set
	 */
	public void setQ3(double q3) {
		this.q3 = q3;
	}
}