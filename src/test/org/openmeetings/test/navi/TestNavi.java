package org.openmeetings.test.navi;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.app.hibernate.beans.basic.*;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;
import junit.framework.TestCase;

public class TestNavi extends TestCase {
	
	public TestNavi(String testname){
		super(testname);
	}
	
	public void testGetNavi(){
		
		MainService mService = new MainService();
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), "SebastianWagner", "test",false,null,"localhost");
		
		System.out.println("us: "+us.getFirstname());
		
        for (Iterator<Organisation_Users> iter = us.getOrganisation_users()
                .iterator(); iter.hasNext();) {

            Long organization_id = iter.next().getOrganisation()
                    .getOrganisation_id();
            List ll = mService.getNavi(sessionData.getSession_id(), 1, organization_id);

            System.out.println("NaviGlobal size: " + ll.size());

            for (Iterator it2 = ll.iterator(); it2.hasNext();) {
                Naviglobal navigl = (Naviglobal) it2.next();
                System.out.println(navigl.getLabel().getValue());
                Set s = navigl.getMainnavi();

                for (Iterator it3 = s.iterator(); it3.hasNext();) {
                    Navimain navim = (Navimain) it3.next();
                    System.out.println("-->" + navim.getLabel().getValue());

                    for (Iterator it4 = navim.getSubnavi().iterator(); it4
                            .hasNext();) {
                        Navisub navis = (Navisub) it4.next();
                        System.out.println("---->"
                                + navis.getLabel().getValue());
                    }

                }
            }
        }
		
	}

}
