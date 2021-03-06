/*=============================================================================
* Result.java
* Represents the result of 1p JSON-RPC.
*==============================================================================
*
* Tested with JDK 1.6
*
* Copyright (c) 2011, Exosite LLC
* All rights reserved.
*/

package onepv1;

public class Result {
	private Object status_;
	private String message_;

	Result(Object status, String message) {
		this.status_ = status;
		this.message_ = message;
	}

	public Object getStatus() {
		return status_;
	}

	public String getMessage() {
		return message_;
	}

	public final static String OK = "ok";
	public final static String FAIL = "fail";
}
