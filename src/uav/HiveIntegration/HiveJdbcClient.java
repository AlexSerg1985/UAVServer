
/**The MIT License (MIT)
Copyright (c) 2018 by AleksanderSergeevich
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package uav.HiveIntegration;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@PropertySource("classpath:Hive.properties")
public class HiveJdbcClient {
    static final String patternCS = "[/@]+([^:/@]+)([:/]+|$)";
    private static Logger logger = Logger.getLogger(HiveJdbcClient.class.getName());
    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
    private Connection connection;

    private static final String defaultConStr = "jdbc:hive://localhost:10000/default";
    @Value("${Hive.connectionStr}")
    private String conStr = "";
    @Value("${Hive.userName}")
    private String userName = "";
    @Value("${Hive.password}")
    private String password = "";

    public void setConnString(String conStr_) {
        this.conStr = conStr_;
    }
    public String getConnString() {
        return this.conStr;
    }
    public void setUserName(String userName_) {
        this.userName = userName_;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setPassword(String password_) {
        this.password = password_;
    }
    public String getPassword() {
        return this.password;
    }
    static boolean isValidConStr(String conStr) {
        if(conStr == null || conStr.isEmpty()) return false;
        if(!conStr.matches(patternCS)) return false;
        return true;
    }
    public Boolean setupConnection() throws SQLException {
        try {
            Class.forName(driverName);
            if(isValidConStr(conStr))
                connection = DriverManager.getConnection(conStr, userName, password);
            else
                connection = DriverManager.getConnection(defaultConStr, userName, password);
        }
        catch(ClassNotFoundException ex) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Exception occur : ", ex);
            }
            return false;
        }
        catch(Exception ex) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Exception occur : ", ex);
            }
            return false;
        }
        return true;
    }
    public void close() {
        if(this.connection != null) {
            try {
                this.connection.close();
            }
            catch(java.sql.SQLException ex) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "Exception occur : ", ex);
                }
            }
        }
    }
    @Override
    protected void finalize() throws Throwable {
        this.close();
    }
}
