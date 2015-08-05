package pt.altran.script.db;

import org.junit.Assert;

import java.sql.ResultSet;

public class StatementsVerifier implements ResultListener{
    private String statement;

    public void startScript() {
        //do nothing
    }

    public void currentStatement(String statement) {
        this.statement = statement;
    }

    public void setIsNotEmpty(boolean hasResult) {
        Assert.assertTrue("Statement:\n" + statement + "\n is empty!",hasResult);
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

    public void afterRun() {

    }
}
