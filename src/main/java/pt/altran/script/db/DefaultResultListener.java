package pt.altran.script.db;

import java.sql.ResultSet;

public class DefaultResultListener implements ResultListener {
    public void startScript() {
        //do nothing
    }

    public void currentStatement(String statement) {
        //do nothing
    }

    public void setIsNotEmpty(boolean hasResult) {
        //do nothing
    }

    public void currentResultSet(ResultSet resultSet) {
        //do nothing
    }

    public boolean delegateIteration() {
        return true;
    }

    public void setColumnsCount(int cols) {
        //do nothing
    }
}
