package pt.altran.script.db;

import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestScripts {


    private SqlSession session;

    @Before
    public void setup() throws URISyntaxException, FileNotFoundException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
            .build(TestScripts.class.getResourceAsStream("/config.xml"), "h2");

        session = sqlSessionFactory.openSession();
        CustomScriptRunner customScriptRunner = new CustomScriptRunner(session.getConnection());
        final File file = new File(TestScripts.class.getResource("/insert.sql").toURI());
        customScriptRunner.runScript(new FileReader(file));
    }

    @After
    public void tearDown() {
        session.close();
    }

    @Test
    public void compareTwoScripts() throws URISyntaxException, FileNotFoundException {

        final List<DataSet> dataSets = new ArrayList<DataSet>();

        CustomScriptRunner customScriptRunner = new CustomScriptRunner(session.getConnection(), new ResultListener() {
            int cols;
            int scriptIndex = 0;
            int statementIndex;
            int rowIndex;

            public void startScript() {
                scriptIndex++;
                statementIndex = 0;
                rowIndex = 0;
            }

            public void currentStatement(String statement) {
                rowIndex = 0;
                if (scriptIndex < 2) {
                    dataSets.add(new DataSet());
                }
                statementIndex++;
            }

            public void setIsNotEmpty(boolean hasResult) {
            }

            public void currentResultSet(ResultSet resultSet) {
                try {
                    Map<String, Object> line;
                    if (scriptIndex == 2) {
                        line = dataSets.get(statementIndex-1).getLines().get(rowIndex);
                        for (Map.Entry<String, Object> entry : line.entrySet()) {
                            Assert.assertEquals(line.get(entry.getKey()), resultSet.getObject(entry.getKey()));
                        }
                    } else {
                        DataSet dataSet = dataSets.get(dataSets.size() - 1);
                        line = new HashMap<String, Object>();
                        for (int i = 0; i < cols; i++) {
                            String columnName = resultSet.getMetaData().getColumnName(i + 1);
                            Object value = resultSet.getObject(i + 1);
                            line.put(columnName, value);
                        }
                        dataSet.addLine(line);
                    }
                } catch (SQLException e) {
                    throw new RuntimeSqlException(e);
                }
                rowIndex++;
            }

            public void setColumnsCount(int cols) {
                this.cols = cols;
            }
        });
        File file1 = new File(getClass().getResource("/postInit.sql").toURI());
        File file2 = new File(getClass().getResource("/postVerify.sql").toURI());
        customScriptRunner.setLogWriter(null);

        customScriptRunner.runScript(new FileReader(file1));

        customScriptRunner.runScript(new FileReader(file2));

    }

    @Test
    public void compareTwoScripts2() throws URISyntaxException, FileNotFoundException {
        CompareResultListener comparer = new CompareResultListener() {
            @Override
            protected void compare(Map.Entry<String, Object> entry, Map<String, Object> line, ResultSet resultSet) {
                try {
                    Assert.assertEquals(entry.getValue(),resultSet.getObject(entry.getKey()));
                } catch (SQLException e) {
                    throw new RuntimeSqlException(e);
                }
            }
        };


        CustomScriptRunner customScriptRunner = new CustomScriptRunner(session.getConnection(), comparer);
        File file1 = new File(getClass().getResource("/postInit.sql").toURI());
        File file2 = new File(getClass().getResource("/postVerify.sql").toURI());
        customScriptRunner.setLogWriter(null);

        customScriptRunner.runScript(new FileReader(file1));

        customScriptRunner.runScript(new FileReader(file2));
    }

}
