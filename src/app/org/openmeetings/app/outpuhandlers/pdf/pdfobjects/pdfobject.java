package org.openmeetings.app.outpuhandlers.pdf.pdfobjects;

public class pdfobject {
    private String cellname;
    private String cellvalues;
    private boolean html;     
    public pdfobject(String cellname, String cellvalues, boolean html) {
        super();
        // TODO Auto-generated constructor stub
        this.cellname = cellname;
        this.cellvalues = cellvalues;
        this.html = html;
    }
    public String getCellname() {
        return cellname;
    }
    public void setCellname(String cellname) {
        this.cellname = cellname;
    }
    public String getCellvalues() {
        return cellvalues;
    }
    public void setCellvalues(String cellvalues) {
        this.cellvalues = cellvalues;
    }
    public boolean isHtml() {
        return html;
    }
    public void setHtml(boolean html) {
        this.html = html;
    }   
}
