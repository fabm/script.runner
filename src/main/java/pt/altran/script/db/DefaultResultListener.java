package pt.altran.script.db;

import java.sql.ResultSet;

public class DefaultResultListener implements ResultListener {
    public void currentStatement(String statement) {
        //do nothing
    }

    public void setIsNotEmpty(boolean hasResult) {
        //do nothing
    }

    public void currentResultSet(ResultSet resultSet) {
        //do nothing
    }

    public boolean delegatePrint() {
        return true;
    }

    public void setColumnsCount(int cols) {
        //do nothing
    }
}
