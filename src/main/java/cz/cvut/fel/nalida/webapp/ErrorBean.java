package cz.cvut.fel.nalida.webapp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public class ErrorBean {

	private String errorMsg;
	private int errorCode;

	public ErrorBean() {
	}

	public ErrorBean(String errorMsg, int errorCode) {
		super();
		this.errorMsg = errorMsg;
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return this.errorCode + ": " + this.errorMsg;
	}

}