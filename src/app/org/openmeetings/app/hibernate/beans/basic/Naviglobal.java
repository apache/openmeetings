package org.openmeetings.app.hibernate.beans.basic;

import java.util.Set;
import java.util.Date;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;

/**
 * 
 * @hibernate.class table="naviglobal"
 *
 */
public class Naviglobal {
    
	private Long global_id;
	private String name;
	private String icon;
	private Boolean isleaf;
	private Boolean isopen;
	private String action;
	private Date updatetime;
	private Date starttime;
	private String comment;
	private Integer naviorder;
	private Long level_id;
	private String deleted;  
	private Long fieldvalues_id;
	private Long tooltip_fieldvalues_id;
    private Set mainnavi;
    private Fieldlanguagesvalues label;
    private Fieldlanguagesvalues tooltip;

    public Naviglobal() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	/**
     * @hibernate.property
     *  column="action"
     *  type="string"
     */ 
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * @hibernate.property
     *  column="comment"
     *  type="string"
     */ 
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * 
     * @hibernate.id
     *  column="global_id"
     *  generator-class="increment"
     */ 
    public Long getGlobal_id() {
        return global_id;
    }
    public void setGlobal_id(Long global_id) {
        this.global_id = global_id;
    }
    
    /**
     * @hibernate.property
     *  column="icon"
     *  type="string"
     */ 
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    /**
     * @hibernate.property
     *  column="isleaf"
     *  type="boolean"
     */ 
    public Boolean getIsleaf() {
        return isleaf;
    }
    public void setIsleaf(Boolean isleaf) {
        this.isleaf = isleaf;
    }
    
    /**
     * @hibernate.property
     *  column="isopen"
     *  type="boolean"
     */ 
    public Boolean getIsopen() {
        return isopen;
    }
    public void setIsopen(Boolean isopen) {
        this.isopen = isopen;
    }
    
    
    /**
     * @hibernate.property
     *  column="starttime"
     *  type="java.util.Date"
     */  	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
    /**
     * @hibernate.property
     *  column="updatetime"
     *  type="java.util.Date"
     */  	
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
    /**
     * @hibernate.property
     *  column="deleted"
     *  type="string"
     */	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
    
    /**
     * @hibernate.property
     *  column="name"
     *  type="string"
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @hibernate.property
     *  column="naviorder"
     *  type="int"
     */
	public Integer getNaviorder() {
		return naviorder;
	}
	public void setNaviorder(Integer naviorder) {
		this.naviorder = naviorder;
	}
    
    /**
     * @hibernate.property
     *  column="level_id"
     *  type="long"
     */
    public Long getLevel_id() {
        return level_id;
    }
    public void setLevel_id(Long level_id) {
        this.level_id = level_id;
    }
    
    /**
     * @hibernate.set 
     * table = "navimain" 
     * inverse = "true" 
     * cascade = "all"
     * where = "deleted='false'"
     * order-by = "naviorder"
     * lazy="false"
     * @hibernate.one-to-many 
     * class = "org.openmeetings.app.hibernate.beans.basic.Navimain"
     * @hibernate.key 
     * column = "global_id"
     */
    public Set getMainnavi() {
        return mainnavi;
    }
    public void setMainnavi(Set mainnavi) {
        this.mainnavi = mainnavi;
    }

    /**
     * @hibernate.property
     *  column="fieldvalues_id"
     *  type="long"
     */
	public Long getFieldvalues_id() {
		return fieldvalues_id;
	}
	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}

	public Fieldlanguagesvalues getLabel() {
		return label;
	}
	public void setLabel(Fieldlanguagesvalues label) {
		this.label = label;
	}

	/**
     * @hibernate.property
     *  column="tooltip_fieldvalues_id"
     *  type="long"
     */
	public Long getTooltip_fieldvalues_id() {
		return tooltip_fieldvalues_id;
	}
	public void setTooltip_fieldvalues_id(Long tooltip_fieldvalues_id) {
		this.tooltip_fieldvalues_id = tooltip_fieldvalues_id;
	}

	public Fieldlanguagesvalues getTooltip() {
		return tooltip;
	}
	public void setTooltip(Fieldlanguagesvalues tooltip) {
		this.tooltip = tooltip;
	}
	
}
