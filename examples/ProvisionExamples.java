/*=============================================================================
* ProvisionExamples.java
* Use-case examples.
* Note that CIK strings ("PUTA40CHARACTER...") need to be replaced with a valid
* value.
*==============================================================================
*
* Tested with JDK 1.8
*
* Copyright (c) 2015, Exosite LLC
* All rights reserved.
*/

import java.util.HashMap;
import java.util.Random;
import onepv1.*;

public class ProvisionExamples {
	public static void main(String[] args) throws OneException {
        String vendorname = "VENDORNAMEHERE";
        String vendortoken = "VENDORTOKENHERE";
        String clonecik = "CLONECIKHERE";
        String cloneportalcik = "CLONEPORTALCIKHERE"; //use only if managing by sharecode
        String portalcik = "PORTALCIKHERE";

        int r = new Random().nextInt(10000000);
        String model = "MyTestModel" + Integer.toString(r);
        String sn1 = "001" + Integer.toString(r);
        String sn2 = "002" + Integer.toString(r);
        String sn3 = "003" + Integer.toString(r);
        Onep op = new Onep("https://m2.exosite.com/onep:v1/rpc/process", 3);
        Result portalridResult = op.lookup(portalcik, "alias", "");
        String portalrid = null;
        String clonerid = null;
        if (portalridResult.getStatus() == Result.OK)
        {
            portalrid = portalridResult.getMessage();
            System.out.println("\r\nportalrid: " + portalrid);
        }
        else System.out.println("\r\nFailed to look up portal RID");
        Result cloneridResult = op.lookup(clonecik, "alias", "");
        Provision provision = null;
        if (cloneridResult.getStatus() == Result.OK)
        {
            clonerid = cloneridResult.getMessage();
            System.out.println("\r\nclonerid: " + clonerid);
            provision = new Provision("https://m2.exosite.com", 3, false, true);
        }
        else System.out.println("\r\nFailed to look up clone RID");
        HashMap<String, String> meta = new HashMap<String, String>();
        String[] options = new String[2];
        options[0] = vendorname;
        options[1] = model;
        String option = "[" + "\"" + vendorname + "\"" + ", " + "\"" + model + "\"" + "]";
        meta.put("meta", option);
        String sharecode = op.share(cloneportalcik, clonerid, meta).getMessage();
        try
        {
            System.out.println("\r\nmodel_create()");
            System.out.println("\r\n" + provision.model_create(vendortoken, model, sharecode, false, true, true).getMessage());
            System.out.println("\r\nmodel_list()\r\n"+provision.model_list(vendortoken).getMessage());
            System.out.println("\r\nmodel_info()\r\n" + provision.model_info(vendortoken, model).getMessage());
            System.out.println("\r\nserialnumber_add()");
            System.out.println("\r\n" + provision.serialnumber_add(vendortoken, model, sn1).getMessage());
            System.out.println("\r\nserialnumber_add_batch()");
            String[] sn2andsn3 = new String[2];
            sn2andsn3[0] = sn2;
            sn2andsn3[1] = sn3;
            System.out.println("\r\n" + provision.serialnumber_add_batch(vendortoken, model, sn2andsn3).getMessage());
            System.out.println("\r\nserialnumber_list()\r\n" + provision.serialnumber_list(vendortoken, model, 0, 10).getMessage());
            System.out.println("\r\nserialnumber_remove_batch()");
            System.out.println("\r\n" + provision.serialnumber_remove_batch(vendortoken, model, sn2andsn3).getMessage());
            System.out.println("\r\nserialnumber_list()\r\n" + provision.serialnumber_list(vendortoken, model, 0, 1000).getMessage());
            System.out.println("\r\nserialnumber_enable()"); 
            provision.serialnumber_enable(vendortoken, model, sn1, portalrid); //return clientid
            System.out.println("\r\nAFTER ENABLE: " + provision.serialnumber_info(vendortoken, model, sn1).getMessage());
            System.out.println("\r\nserialnumber_disable()");
            provision.serialnumber_disable(vendortoken, model, sn1);
            System.out.println("\r\nAFTER DISABLE: " + provision.serialnumber_info(vendortoken, model, sn1).getMessage());
            System.out.println("\r\nserialnumber_reenable()");
            provision.serialnumber_reenable(vendortoken, model, sn1);
            System.out.println("\r\nAFTER REENABLE: " + provision.serialnumber_info(vendortoken, model, sn1).getMessage());
            System.out.println("\r\nserialnumber_activate()");
            //return client key
            String sn_cik = provision.serialnumber_activate(model, sn1, vendorname).getMessage();
            System.out.println("\r\nAFTER ACTIVATE: " + provision.serialnumber_info(vendortoken, model, sn1).getMessage());

            System.out.println("\r\ncontent_create()");
            System.out.println("\r\n" + provision.content_create(vendortoken, model, "a.txt", "This is text", false).getMessage());
            System.out.println("\r\ncontent_upload()");
            System.out.println("\r\n" + provision.content_upload(vendortoken, model, "a.txt", "This is content data", "text/plain").getMessage());
            System.out.println("\r\ncontent_list()\r\n" + provision.content_list(vendortoken, model).getMessage());
            System.out.println(vendortoken);
            System.out.println(model);
            System.out.println("a.txt");
            System.out.println("\r\ncontent_remove()");
            provision.content_remove(vendortoken, model, "a.txt");

            System.out.println("\r\nmodel_remove()");
            provision.model_remove(vendortoken, model);
        }
        catch (OneException e) 
        {
            System.out.println("\r\nprovisionExample sequence exception:");
            System.out.println(e.getMessage());
        }
	}
}
