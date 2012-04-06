/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.app.persistence.beans.sip.asterisk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sipusers")
public class AsteriskSipUsers implements Serializable {
	private static final long serialVersionUID = -565831761546365623L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "accountcode")
    private String accountcode;

    @Column(name = "disallow")
    private String disallow = null;

    @Column(name = "allow")
    private String allow = "ulaw";

    @Column(name = "allowoverlap")
    private String allowoverlap = "yes";

    @Column(name = "allowsubscribe")
    private String allowsubscribe = "yes";

    @Column(name = "allowtransfer")
    private String allowtransfer;

    @Column(name = "amaflags")
    private String amaflags;

    @Column(name = "autoframing")
    private String autoframing;

    @Column(name = "auth")
    private String auth;

      @Column(name = "buggymwi")
    private String buggymwi = "no";

    @Column(name = "callgroup")
    private String callgroup;

    @Column(name = "callerid")
    private String callerid;

    @Column(name = "cid_number")
    private String cid_number;

    @Column(name = "fullname")
    private String fullname;

//    TODO: error
//    @Column(name = "call-limit")
//    private int callLimit;

    @Column(name = "callingpres")
    private String callingpres;

    @Column(name = "canreinvite")
    private String canreinvite = "yes";

    @Column(name = "context")
    private String context;

    @Column(name = "defaultip")
    private String defaultip;

    @Column(name = "dtmfmode")
    private String dtmfmode;

    @Column(name = "fromuser")
    private String fromuser;

    @Column(name = "fromdomain")
    private String fromdomain;

    @Column(name = "fullcontact")
    private String fullcontact;

//    @Column(name = "g726nonstandard")
    @Column(name = "g726nonstandard")
    //@Enumerated(EnumType.STRING)
    private String g726nonstandard = "no";

    @Column(name = "host", nullable = false)
    private String host = "dynamic";

    @Column(name = "insecure")
    private String insecure;

    @Column(name = "ipaddr", nullable = false)
    private String ipaddr = "";

    @Column(name = "language")
    private String language;

    @Column(name = "lastms")
    private String lastms;

    @Column(name = "mailbox")
    private String mailbox;

    @Column(name = "maxcallbitrate")
    private int maxcallbitrate = 384;

    @Column(name = "mohsuggest")
    private String mohsuggest;

    @Column(name = "md5secret")
    private String md5secret;

    @Column(name = "musiconhold")
    private String musiconhold;

    @Column(name = "name", nullable = false)
    private String name = "";

    @Column(name = "nat", nullable = false)
    private String nat = "no";

    @Column(name = "outboundproxy")
    private String outboundproxy;

    @Column(name = "deny")
    private String deny;

    @Column(name = "permit")
    private String permit;

    @Column(name = "pickupgroup")
    private String pickupgroup;

    @Column(name = "port", nullable = false)
    private String port = "";

//    @Column(name = "progressinband")
    @Column(name = "progressinband")
    //@Enumerated(EnumType.STRING)
    private String progressinband = "no";
//    private YesNoNever progressinband;

//    @Column(name = "promiscredir")
    @Column(name = "promiscredir")
    //@Enumerated(EnumType.STRING)
    private String promiscredir = "no";

    @Column(name = "qualify")
    private String qualify;

    @Column(name = "regexten", nullable = false)
    private String regexten = "";

    @Column(name = "regseconds", nullable = false)
    private int regseconds = 0;

//    @Column(name = "rfc2833compensate")
    @Column(name = "rfc2833compensate")
    //@Enumerated(EnumType.STRING)
    private String rfc2833compensate = "no";

    @Column(name = "rtptimeout")
    private String rtptimeout;

    @Column(name = "rtpholdtimeout")
    private String rtpholdtimeout;

    @Column(name = "secret")
    private String secret;

//    @Column(name = "sendrpid")
    @Column(name = "sendrpid")
    //@Enumerated(EnumType.STRING)
    private String sendrpid = "yes";

    @Column(name = "setvar", nullable = false)
    private String setvar = "";

    @Column(name = "subscribecontext")
    private String subscribecontext;

    @Column(name = "subscribemwi")
    private String subscribemwi;

//    @Column(name = "t38pt_udptl")
    @Column(name = "t38pt_udptl")
    //@Enumerated(EnumType.STRING)
    private String t38pt_udptl = "no";

//    @Column(name = "trustrpid")
    @Column(name = "trustrpid")
    //@Enumerated(EnumType.STRING)
    private String trustrpid = "no";

    @Column(name = "type", nullable = false)
    private String type = "friend";

//    @Column(name = "useclientcode")
    @Column(name = "useclientcode")
    //@Enumerated(EnumType.STRING)
    private String useclientcode = "no";

    @Column(name = "username", nullable = false)
    private String username = "";

    @Column(name = "usereqphone", nullable = false)
    private String usereqphone = "no";

//    @Column(name = "videosupport")
    @Column(name = "videosupport")
    //@Enumerated(EnumType.STRING)
    private String videosupport = "yes";

    @Column(name = "vmexten")
    private String vmexten;

//    create unique index name on asterisk.sipusers (name);
//    create index name_2 on asterisk.sipusers (name);


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountcode() {
        return accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode;
    }

    public String getDisallow() {
        return disallow;
    }

    public void setDisallow(String disallow) {
        this.disallow = disallow;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    public String getAllowoverlap() {
        return allowoverlap;
    }

    public void setAllowoverlap(String allowoverlap) {
        this.allowoverlap = allowoverlap;
    }

    public String getAllowsubscribe() {
        return allowsubscribe;
    }

    public void setAllowsubscribe(String allowsubscribe) {
        this.allowsubscribe = allowsubscribe;
    }

    public String getAllowtransfer() {
        return allowtransfer;
    }

    public void setAllowtransfer(String allowtransfer) {
        this.allowtransfer = allowtransfer;
    }

    public String getAmaflags() {
        return amaflags;
    }

    public void setAmaflags(String amaflags) {
        this.amaflags = amaflags;
    }

    public String getAutoframing() {
        return autoframing;
    }

    public void setAutoframing(String autoframing) {
        this.autoframing = autoframing;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getBuggymwi() {
        return buggymwi;
    }

    public void setBuggymwi(String buggymwi) {
        this.buggymwi = buggymwi;
    }

    public String getCallgroup() {
        return callgroup;
    }

    public void setCallgroup(String callgroup) {
        this.callgroup = callgroup;
    }

    public String getCallerid() {
        return callerid;
    }

    public void setCallerid(String callerid) {
        this.callerid = callerid;
    }

    public String getCid_number() {
        return cid_number;
    }

    public void setCid_number(String cid_number) {
        this.cid_number = cid_number;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCallingpres() {
        return callingpres;
    }

    public void setCallingpres(String callingpres) {
        this.callingpres = callingpres;
    }

    public String getCanreinvite() {
        return canreinvite;
    }

    public void setCanreinvite(String canreinvite) {
        this.canreinvite = canreinvite;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDefaultip() {
        return defaultip;
    }

    public void setDefaultip(String defaultip) {
        this.defaultip = defaultip;
    }

    public String getDtmfmode() {
        return dtmfmode;
    }

    public void setDtmfmode(String dtmfmode) {
        this.dtmfmode = dtmfmode;
    }

    public String getFromuser() {
        return fromuser;
    }

    public void setFromuser(String fromuser) {
        this.fromuser = fromuser;
    }

    public String getFromdomain() {
        return fromdomain;
    }

    public void setFromdomain(String fromdomain) {
        this.fromdomain = fromdomain;
    }

    public String getFullcontact() {
        return fullcontact;
    }

    public void setFullcontact(String fullcontact) {
        this.fullcontact = fullcontact;
    }

    public String getG726nonstandard() {
        return g726nonstandard;
    }

    public void setG726nonstandard(String g726nonstandard) {
        this.g726nonstandard = g726nonstandard;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getInsecure() {
        return insecure;
    }

    public void setInsecure(String insecure) {
        this.insecure = insecure;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastms() {
        return lastms;
    }

    public void setLastms(String lastms) {
        this.lastms = lastms;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public int getMaxcallbitrate() {
        return maxcallbitrate;
    }

    public void setMaxcallbitrate(int maxcallbitrate) {
        this.maxcallbitrate = maxcallbitrate;
    }

    public String getMohsuggest() {
        return mohsuggest;
    }

    public void setMohsuggest(String mohsuggest) {
        this.mohsuggest = mohsuggest;
    }

    public String getMd5secret() {
        return md5secret;
    }

    public void setMd5secret(String md5secret) {
        this.md5secret = md5secret;
    }

    public String getMusiconhold() {
        return musiconhold;
    }

    public void setMusiconhold(String musiconhold) {
        this.musiconhold = musiconhold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNat() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat = nat;
    }

    public String getOutboundproxy() {
        return outboundproxy;
    }

    public void setOutboundproxy(String outboundproxy) {
        this.outboundproxy = outboundproxy;
    }

    public String getDeny() {
        return deny;
    }

    public void setDeny(String deny) {
        this.deny = deny;
    }

    public String getPermit() {
        return permit;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }

    public String getPickupgroup() {
        return pickupgroup;
    }

    public void setPickupgroup(String pickupgroup) {
        this.pickupgroup = pickupgroup;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProgressinband() {
        return progressinband;
    }

    public void setProgressinband(String progressinband) {
        this.progressinband = progressinband;
    }

    public String getPromiscredir() {
        return promiscredir;
    }

    public void setPromiscredir(String promiscredir) {
        this.promiscredir = promiscredir;
    }

    public String getQualify() {
        return qualify;
    }

    public void setQualify(String qualify) {
        this.qualify = qualify;
    }

    public String getRegexten() {
        return regexten;
    }

    public void setRegexten(String regexten) {
        this.regexten = regexten;
    }

    public int getRegseconds() {
        return regseconds;
    }

    public void setRegseconds(int regseconds) {
        this.regseconds = regseconds;
    }

    public String getRfc2833compensate() {
        return rfc2833compensate;
    }

    public void setRfc2833compensate(String rfc2833compensate) {
        this.rfc2833compensate = rfc2833compensate;
    }

    public String getRtptimeout() {
        return rtptimeout;
    }

    public void setRtptimeout(String rtptimeout) {
        this.rtptimeout = rtptimeout;
    }

    public String getRtpholdtimeout() {
        return rtpholdtimeout;
    }

    public void setRtpholdtimeout(String rtpholdtimeout) {
        this.rtpholdtimeout = rtpholdtimeout;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSendrpid() {
        return sendrpid;
    }

    public void setSendrpid(String sendrpid) {
        this.sendrpid = sendrpid;
    }

    public String getSetvar() {
        return setvar;
    }

    public void setSetvar(String setvar) {
        this.setvar = setvar;
    }

    public String getSubscribecontext() {
        return subscribecontext;
    }

    public void setSubscribecontext(String subscribecontext) {
        this.subscribecontext = subscribecontext;
    }

    public String getSubscribemwi() {
        return subscribemwi;
    }

    public void setSubscribemwi(String subscribemwi) {
        this.subscribemwi = subscribemwi;
    }

    public String getT38pt_udptl() {
        return t38pt_udptl;
    }

    public void setT38pt_udptl(String t38pt_udptl) {
        this.t38pt_udptl = t38pt_udptl;
    }

    public String getTrustrpid() {
        return trustrpid;
    }

    public void setTrustrpid(String trustrpid) {
        this.trustrpid = trustrpid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUseclientcode() {
        return useclientcode;
    }

    public void setUseclientcode(String useclientcode) {
        this.useclientcode = useclientcode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsereqphone() {
        return usereqphone;
    }

    public void setUsereqphone(String usereqphone) {
        this.usereqphone = usereqphone;
    }

    public String getVideosupport() {
        return videosupport;
    }

    public void setVideosupport(String videosupport) {
        this.videosupport = videosupport;
    }

    public String getVmexten() {
        return vmexten;
    }

    public void setVmexten(String vmexten) {
        this.vmexten = vmexten;
    }
}