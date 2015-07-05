package pt.altran.script.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;

public class TestScripts {


    private static SqlSession session;

    @BeforeClass
    public static void setup() throws URISyntaxException, FileNotFoundException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
            .build(TestScripts.class.getResourceAsStream("/config.xml"),"h2");

        session = sqlSessionFactory.openSession();
        CustomScriptRunner customScriptRunner = new CustomScriptRunner(session.getConnection());
        final File file = new File(TestScripts.class.getResource("/insert.sql").toURI());
        customScriptRunner.runScript(new FileReader(file));
    }

    @AfterClass
    public static void tearDown(){
        session.close();
    }

    @Test
    public void testScriptOk() throws URISyntaxException, FileNotFoundException {
        CustomScriptRunner customScriptRunner = new CustomScriptRunner(session.getConnection(),new StatementsVerifier());
        File file = new File(getClass().getResource("/verifiedSql1.sql").toURI());
        customScriptRunner.runScript(new FileReader(file));
    }

    @Test
    public void testScriptNotOk() throws URISyntaxException, FileNotFoundException {
        CustomScriptRunner customScriptRunner = new CustomScriptRunner(session.getConnection(), new StatementsVerifier());
        File file = new File(getClass().getResource("/verifiedSql2.sql").toURI());
        boolean assertFailure = false;
        try {
            customScriptRunner.runScript(new FileReader(file));
        } catch (AssertionError e) {
            assertFailure = true;
            System.out.println("Test fails but error is caught");
        }

        Assert.assertTrue("Expected assert failure in runScript method", assertFailure);
    }
}
