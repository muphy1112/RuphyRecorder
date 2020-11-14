package me.muphy.entity;

import java.util.Date;

public class ClientVisitInfoEntity {

	private String ip;
	private String visitInfo;
	private Date visitTime;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getVisitInfo() {
		return visitInfo;
	}

	public void setVisitInfo(String visitInfo) {
		this.visitInfo = visitInfo;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}

}
