package org.scigap.iucig.gateway.util;

import org.apache.airavata.common.utils.DBUtil;


public class DBConnector extends DBUtil {
    public DBConnector(String jdbcUrl, String userName, String password, String driver) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        super(jdbcUrl, userName, password, driver);
    }


}
