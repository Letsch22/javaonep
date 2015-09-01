/*=============================================================================
* ProvisionResponseException.java
* Exception class for Provision failure
*==============================================================================
*
* Tested with JDK 1.8
*
* Copyright (c) 2015, Exosite LLC
* All rights reserved.
*/

package onepv1;

@SuppressWarnings("serial")
public class ProvisionResponseException extends OneException {

	public ProvisionResponseException(final String message) {
		super(message);
	}
}
