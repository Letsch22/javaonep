/*=============================================================================
* Provision.java
* Provides simple use for Provisioning API procedures.
*==============================================================================
*
* Tested with JDK 1.8
*
* Copyright (c) 2015, Exosite LLC
* All rights reserved.
*/

package onepv1;

import java.util.HashMap;

public class Provision {
	public static String PROVISION_BASE = "/provision";
    public static String PROVISION_ACTIVATE = PROVISION_BASE + "/activate";
    public static String PROVISION_DOWNLOAD = PROVISION_BASE + "/download";
    public static String PROVISION_MANAGE = PROVISION_BASE + "/manage";
    public static String PROVISION_MANAGE_MODEL = PROVISION_MANAGE + "/model/";
    public static String PROVISION_MANAGE_CONTENT = PROVISION_MANAGE + "/content/";
    public static String PROVISION_REGISTER = PROVISION_BASE + "/register";
    
    private HttpTransport transport;
    private boolean manage_by_cik;
    private boolean manage_by_sharecode;
    
    public Provision(String url, int timeout, boolean managebycik, boolean managebysharecode) {
    	transport = new HttpTransport(url, timeout);
    	manage_by_cik = managebycik;
    	manage_by_sharecode = managebysharecode;
    }
    
    static private Result parseProvisionResponse(String res, Integer statuscode)
    {
        if (statuscode == 200 || statuscode == 204 || statuscode == 205)
        {
            return new Result(statuscode, res);
        }
        else
        {
            return new Result(Result.FAIL, res);
        }
    }
    
    private Result request(String path, String key, String data, String method, boolean key_is_cik, HashMap<String, String> extra_headers) 
    		throws ProvisionRequestException, ProvisionResponseException
    {
        String url, body;
        if (method == "GET")
        {
            if (data.length() > 0)
                url = path + "?" + data;
            else
                url = path;
            body = null;    
        }
        else
        {
            url = path;
            body = data;
        }
        HashMap<String, String> headers = new HashMap<String, String>();
        if (key_is_cik)
            headers.put("X-Exosite-CIK", key);
        else
            headers.put("X-Exosite-Token", key);
        if (method == "POST")
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.put("Accept", "text/plain, text/csv, application/x-www-form-urlencoded");
        if (extra_headers != null) {
        	if (!extra_headers.isEmpty()) { 
        		for (String webkey : extra_headers.keySet())
        			headers.put(webkey, extra_headers.get(webkey));
        	}
        }
        HashMap<String,Integer> response = transport.provisionSend(body, method, url, headers);
        String keys[] = new String[1];
        Integer values[] = new Integer[1];
        return parseProvisionResponse(response.keySet().toArray(keys)[0], response.values().toArray(values)[0]);
    }
    
    public Result content_create(String key, String model, String contentid, String meta, boolean protect) 
    		throws ProvisionRequestException, ProvisionResponseException {
    	String data = "id=" + contentid + "&meta=" + meta;
    	if (protect != false) data = data + "&protected=true";
    	String path = PROVISION_MANAGE_CONTENT + model + "/";
    	return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result content_download(String cik, String vendor, String model, String contentid) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "vendor=" + vendor + "&model=" + model + "&id=" + contentid;
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "*");
		return request(PROVISION_DOWNLOAD, cik, data, "GET", true, headers);
    }
    
    public Result content_info(String key, String model, String contentid, String vendor) 
    		throws ProvisionRequestException, ProvisionResponseException {
		if (vendor == "") {
			String path = PROVISION_MANAGE_CONTENT + model + "/" + contentid;
			return request(path, key, "", "GET", manage_by_cik, null);
		}
		else {
			String data = "vendor=" + vendor + "&model=" + model + "&info=true";
			return request(PROVISION_DOWNLOAD, key, data, "GET", manage_by_cik, null);
		}
    }
		
	public Result content_list(String key, String model) 
			throws ProvisionRequestException, ProvisionResponseException {
        String path = PROVISION_MANAGE_CONTENT + model + "/";
        return request(path, key, "", "GET", manage_by_cik, null);
	}
	
	public Result content_remove(String key, String model, String contentid) 
			throws ProvisionRequestException, ProvisionResponseException {
        String path = PROVISION_MANAGE_CONTENT + model + "/" + contentid;
        return request(path, key, "", "DELETE", manage_by_cik, null);
	}
	
	public Result content_upload(String key, String model, String contentid, String data, String mimetype) 
			throws ProvisionRequestException, ProvisionResponseException {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", mimetype);
        String path = PROVISION_MANAGE_CONTENT + model + "/" + contentid;
        return request(path, key, data, "POST", manage_by_cik, headers);
	}
	
	public Result model_create(String key, String model, String sharecode, boolean aliases, boolean comments, boolean historical) 
			throws ProvisionRequestException, ProvisionResponseException {
		String data = "model=" + model;
		if (manage_by_sharecode) data = data + "&code=" + sharecode;
		else data = data + "&rid=" + sharecode;
		if (aliases == false) data = data + "&options[]=noaliases";
		if (comments == false) data = data + "&options[]=nocomments";
		if (historical == false) data = data + "&options[]=nohistorical";
		return request(PROVISION_MANAGE_MODEL, key, data, "POST", manage_by_cik, null);
	}
	
    public Result model_info(String key, String model) 
    		throws ProvisionRequestException, ProvisionResponseException
    {
        return request(PROVISION_MANAGE_MODEL + model, key, "", "GET", manage_by_cik, null);
    }
    
    public Result model_list(String key) 
    		throws ProvisionRequestException, ProvisionResponseException
    {
        return request(PROVISION_MANAGE_MODEL, key, "", "GET", manage_by_cik, null);
    }
    
    public Result model_remove(String key, String model) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "delete=true&model=" + model + "&confirm=true";
		String path = PROVISION_MANAGE_MODEL + model;
		return request(path, key, data, "DELETE", manage_by_cik, null);
    }
    
    public Result model_update(String key, String model, String clonerid, boolean aliases, boolean comments, boolean historical) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "rid=" + clonerid;
		if (aliases == false) data = data + "&options[]=noaliases";
		if (comments == false) data = data + "&options[]=nocomments";
		if (historical == false) data = data + "&options[]=nohistorical";
		String path = PROVISION_MANAGE_MODEL + model;
		return request(path, key, data, "PUT", manage_by_cik, null);
    }
    
    public Result serialnumber_activate(String model, String serialnumber, String vendor) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "vendor=" + vendor + "&model=" + model + "&sn=" + serialnumber;
		return request(PROVISION_ACTIVATE, "", data, "POST", manage_by_cik, null);
    }
    
    public Result serialnumber_add(String key, String model, String sn) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "add=true&sn=" + sn;
		String path = PROVISION_MANAGE_MODEL + model + "/";
		return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result serialnumber_add_batch(String key, String model, String[] sns) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "add=true";
		for (String sn : sns) {
			data = data + "&sn[]=" + sn;
		}
		String path = PROVISION_MANAGE_MODEL + model + "/";
		return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result serialnumber_disable(String key, String model, String serialnumber) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "disable=true";
		String path = PROVISION_MANAGE_MODEL + model + "/" + serialnumber;
		return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result serialnumber_enable(String key, String model, String serialnumber, String owner) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "enable=true&owner=" + owner;
		String path = PROVISION_MANAGE_MODEL + model + "/" + serialnumber;
		return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result serialnumber_info(String key, String model, String serialnumber) 
    		throws ProvisionRequestException, ProvisionResponseException {
        String path = PROVISION_MANAGE_MODEL + model + "/" + serialnumber;
        return request(path, key, "", "GET", manage_by_cik, null);
    }
    
    public Result serialnumber_list(String key, String model, int offset, int limit) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "offset=" + Integer.toString(offset) + "&limit=" + Integer.toString(limit);
		String path = PROVISION_MANAGE_MODEL + model + "/";
		return request(path, key, data, "GET", manage_by_cik, null);
    }
    
    public Result serialnumber_reenable(String key, String model, String serialnumber) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "enable=true";
		String path = PROVISION_MANAGE_MODEL + model + "/" + serialnumber;
		return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result serialnumber_remap(String key, String model, String serialnumber, String oldsn) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "enable=true&oldsn=" + oldsn;
		String path = PROVISION_MANAGE_MODEL + model + "/" + serialnumber;
		return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result serialnumber_remove(String key, String model, String serialnumber) 
    		throws ProvisionRequestException, ProvisionResponseException
    {
        String path = PROVISION_MANAGE_MODEL + model + "/" + serialnumber;
        return request(path, key, "", "DELETE", manage_by_cik, null);
    }
    
    public Result serialnumber_remove_batch(String key, String model, String[] sns) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "remove=true";
		for (String sn : sns) {
			data = data + "&sn[]=" + sn;
		}
		String path = PROVISION_MANAGE_MODEL + model + "/";
        return request(path, key, data, "POST", manage_by_cik, null);
    }
    
    public Result vendor_register(String key, String vendor) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "vendor=" + vendor;
		return request(PROVISION_REGISTER, key, data, "POST", manage_by_cik, null);
    }
    
    public Result vendor_show(String key) 
    		throws ProvisionRequestException, ProvisionResponseException
    {
        return request(PROVISION_REGISTER, key, "", "GET", false, null);
    }
    
    public Result vendor_unregister(String key, String vendor) 
    		throws ProvisionRequestException, ProvisionResponseException {
		String data = "delete=true&vendor=" + vendor;
		return request(PROVISION_REGISTER, key, data, "POST", false, null);
    }
}