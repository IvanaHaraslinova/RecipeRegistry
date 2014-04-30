/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DBUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Majo
 */
public class SQLScriptManager {
    
    final static Logger log = LoggerFactory.getLogger(SQLScriptManager.class);
    
    /**
     * Executes SQL script.
     * 
     * @param ds datasource
     * @param scriptUrl url of sql script to be executed
     * @throws SQLException when operation fails
     */
    public static void executeSqlScript(DataSource ds, URL scriptUrl) throws SQLException {
        log.debug("executeSqlScript()");
        try (Connection conn = ds.getConnection()){
            for (String sqlStatement : readSqlStatements(scriptUrl)) {
                if (!sqlStatement.trim().isEmpty()) {
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        } 
    }
    
    /**
     * Reads SQL statements from file. SQL commands in file must be separated by 
     * a semicolon.
     * 
     * @param url url of the file
     * @return array of command  strings
     */
    private static String[] readSqlStatements(URL url) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read " + url, ex);
        }
    }
}
