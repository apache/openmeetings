package org.openmeetings.app.hibernate.utils;

import org.hibernate.dialect.MySQL5Dialect;

public class MySQL5MyISAMDialect extends MySQL5Dialect {

    @Override
    public String getTableTypeString() {
        return " ENGINE=MyISAM";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }
}
