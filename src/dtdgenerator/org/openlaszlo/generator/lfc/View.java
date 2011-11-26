package org.openlaszlo.generator.lfc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class View extends Node {

	@XmlAttribute
	public Boolean aaactive;
	@XmlAttribute
	public String aadescription;
	@XmlAttribute
	public String aaname;
	@XmlAttribute
	public Boolean aasilent;
	@XmlAttribute
	public Integer aatabindex;
	@XmlEnum(String.class)
	public enum align  { left, center, right };
	@XmlAttribute
	public String backgroundrepeat;
	@XmlAttribute
	public Integer bgcolor;
	@XmlAttribute
	public Boolean cachebitmap;
	@XmlAttribute
	public String capabilities;
	@XmlAttribute
	public Boolean clickable;
	@XmlAttribute
	public String clickregion;
	@XmlAttribute
	public Boolean clip;
	@XmlAttribute
	public String colortransform;
	@XmlAttribute
	public String context;
	@XmlAttribute
	public String contextmenu;
	@XmlAttribute
	public String cornerradius;
	@XmlAttribute
	public String cursor;
	@XmlAttribute
	public String fgcolor;
	@XmlAttribute
	public Boolean focusable;
	@XmlAttribute
	public Boolean focustrap;
	@XmlAttribute
	public String font;
	@XmlAttribute
	public Integer fontsize;
	@XmlEnum
	public enum fontstyle { plain,bold,italic,bolditalic};
	@XmlAttribute
	public Integer frame;
	@XmlAttribute
	public Integer framesloadratio;
	@XmlAttribute
	public Boolean hasdirectionallayout;
	@XmlAttribute
	public Boolean hassetheight;
	@XmlAttribute
	public Boolean hassetwidth;
	@XmlAttribute
	public Float height;
	@XmlAttribute
	public String layout;
	@XmlAttribute
	public Float loadratio;
	@XmlAttribute
	public String mask;
	@XmlAttribute
	public Float opacity;
	@XmlAttribute
	public Boolean pixellock;
	@XmlAttribute
	public Boolean playing;
	@XmlAttribute
	public String proxyurl;
	@XmlAttribute
	public String resource;
	@XmlAttribute
	public Float resourceheight;
	@XmlAttribute
	public Float resourcewidth;
	@XmlAttribute
	public Float rotation;
	@XmlAttribute
	public Float shadowangle;
	@XmlAttribute
	public Float shadowblurradius;
	@XmlAttribute
	public String shadowcolor;
	@XmlAttribute
	public Integer shadowdistance;
	@XmlAttribute
	public Boolean showhandcursor;
	@XmlAttribute
	public String source;
	@XmlEnum
	public enum stretches { width, height, both, none };
	@XmlAttribute
	public String subviews;
	@XmlAttribute
	public String tintcolor;
	@XmlAttribute
	public Integer totalframes;
	@XmlAttribute
	public Integer unstretchedheight;
	@XmlAttribute
	public Integer unstretchedwidth;
	@XmlAttribute
	public Boolean usegetbounds;
	@XmlEnum
	public enum valign { top, middle, bottom };
	@XmlAttribute
	public String visibility;
	@XmlAttribute
	public Boolean visible;
	@XmlAttribute
	public Float width;
	@XmlAttribute
	public Float x;
	@XmlAttribute
	public Float xoffset;
	@XmlAttribute
	public Float xscale;
	@XmlAttribute
	public Float y;
	@XmlAttribute
	public Float yoffset;
	@XmlAttribute
	public Float yscale;
	@XmlAttribute
	public String onaddsubview;
	@XmlAttribute
	public String onbackgroundrepeat;
	@XmlAttribute
	public String onblur;
	@XmlAttribute
	public String onclick;
	@XmlAttribute
	public String onclickable;
	@XmlAttribute
	public String onclip;
	@XmlAttribute
	public String oncontext;
	@XmlAttribute
	public String oncornerradius;
	@XmlAttribute
	public String ondblclick;
	@XmlAttribute
	public String onerror;
	@XmlAttribute
	public String onfocus;
	@XmlAttribute
	public String onframe;
	@XmlAttribute
	public String onframesloadratio;
	@XmlAttribute
	public String ongesture;
	@XmlAttribute
	public String onheight;
	@XmlAttribute
	public String onkeydown;
	@XmlAttribute
	public String onkeyup;
	@XmlAttribute
	public String onlastframe;
	@XmlAttribute
	public String onload;
	@XmlAttribute
	public String onloadratio;
	@XmlAttribute
	public String onmousedown;
	@XmlAttribute
	public String onmousedragin;
	@XmlAttribute
	public String onmousedragout;
	@XmlAttribute
	public String onmouseout;
	@XmlAttribute
	public String onmouseover;
	@XmlAttribute
	public String onmousetrackout;
	@XmlAttribute
	public String onmousetrackover;
	@XmlAttribute
	public String onmousetrackup;
	@XmlAttribute
	public String onmouseup;
	@XmlAttribute
	public String onmouseupoutside;
	@XmlAttribute
	public String onopacity;
	@XmlAttribute
	public String onplay;
	@XmlAttribute
	public String onplaying;
	@XmlAttribute
	public String onremovesubview;
	@XmlAttribute
	public String onshadowangle;
	@XmlAttribute
	public String onshadowblurradius;
	@XmlAttribute
	public String onshadowcolor;
	@XmlAttribute
	public String onshadowdistance;
	@XmlAttribute
	public String onstop;
	@XmlAttribute
	public String ontimeout;
	@XmlAttribute
	public String ontouch;
	@XmlAttribute
	public String onvisible;
	@XmlAttribute
	public String onwidth;
	@XmlAttribute
	public String onx;
	@XmlAttribute
	public String ony;

}
