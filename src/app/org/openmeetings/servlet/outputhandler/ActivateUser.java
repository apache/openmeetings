package org.openmeetings.servlet.outputhandler;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.user.Users;

public class ActivateUser extends VelocityViewServlet {

        /**
         * 
         */
        private static final long serialVersionUID = -8892729047921796170L;
        private static final Log log = LogFactory.getLog(ActivateUser.class);

        @Override
        public Template handleRequest(HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse, Context ctx) throws ServletException,
                        IOException {
                
                try {
                        String hash = httpServletRequest.getParameter("u");
                        
                        if (hash == null) {
                                //No hash
                                Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
                                        getConfKey(3,"default_lang_id").getConf_value()).longValue();
                                Fieldlanguagesvalues labelid669 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(669), default_lang_id);
                                Fieldlanguagesvalues labelid672 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(672), default_lang_id);
                                
                                ctx.put("message", labelid669.getValue());
                                ctx.put("link", "<a href='/'>"+ labelid672.getValue() + "</a>");
                                return getVelocityEngine().getTemplate("activation_template.vm");
                        }
                        //
                        Users user = Usermanagement.getInstance().getUserByActivationHash(hash);
                        
                        if (user == null) {
                                //No User Found with this Hash
                                Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
                                        getConfKey(3,"default_lang_id").getConf_value()).longValue();
                                
                                Fieldlanguagesvalues labelid669 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(669), default_lang_id);
                                Fieldlanguagesvalues labelid672 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(672), default_lang_id);
                                
                                ctx.put("message", labelid669.getValue());
                                ctx.put("link", "<a href='/'>"+ labelid672.getValue() + "</a>");
                                return getVelocityEngine().getTemplate("activation_template.vm");
                                
                        } else if (user.getStatus() == 1) {
                                //already activated
                                Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
                                        getConfKey(3,"default_lang_id").getConf_value()).longValue();
                                
                                Fieldlanguagesvalues labelid670 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(670), default_lang_id);
                                Fieldlanguagesvalues labelid672 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(672), default_lang_id);
                                
                                ctx.put("message", labelid670.getValue());
                                ctx.put("link", "<a href='/'>"+ labelid672.getValue() + "</a>");
                                return getVelocityEngine().getTemplate("activation_template.vm");
                                
                        } else if (user.getStatus() == 0) {
                                //activate
                                user.setStatus(1);
                                user.setUpdatetime(new Date());

                                Usermanagement.getInstance().updateUser(user);
                                
                                Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
                                        getConfKey(3,"default_lang_id").getConf_value()).longValue();
                                
                                Fieldlanguagesvalues labelid671 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(671), default_lang_id);
                                Fieldlanguagesvalues labelid672 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(672), default_lang_id);
                                
                                ctx.put("message", labelid671.getValue());
                                ctx.put("link", "<a href='/'>"+ labelid672.getValue() + "</a>");
                                return getVelocityEngine().getTemplate("activation_template.vm");
                                
                        } else {
                                //unkown Status
                                Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
                                        getConfKey(3,"default_lang_id").getConf_value()).longValue();
                                
                                Fieldlanguagesvalues labelid672 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(672), default_lang_id);
                                
                                ctx.put("message", "Unkown Status");
                                ctx.put("link", "<a href='/'>"+ labelid672.getValue() + "</a>");
                                return getVelocityEngine().getTemplate("activation_template.vm");
                                
                        }
                        
                } catch (Exception err) {
                        log.error("[ActivateUser]",err);
                        err.printStackTrace();
                }
                return null;
        }

}

