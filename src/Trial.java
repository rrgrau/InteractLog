// This class holds basic session information for structured data storage

public class Trial {

	private String participantID;
	private String experimentSessionID;
	private int trialnr;
	
 
	public Trial(int trialnr) {
		this.trialnr = trialnr;
	}
	
	@Override
	public String toString() {
		String s = participantID + " - " +experimentSessionID;
		return s;
	}
	
	public String getExperimentSessionID() {
		return experimentSessionID;
	}

	public void setExperimentSessionID(String experimentSessionID) {
		this.experimentSessionID = experimentSessionID;
	}

	public String getParticipantID() {
		return participantID;
	}

	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}
	public int getTrialnr() {
		return trialnr;
	}
	public void setTrialnr(int n) {
		this.trialnr = n;
	}
}
