import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;


public class JobService
{
    /**
     * Insert new record into executions table.
     * @param  job  {@link Job} object to be inserted
     */
    public static void insert(Job job)
    {
        Connection conn = DBConn.establishConn();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("insert into jobs" +
                    "( command, month, m_day, w_day, hour, minute )" +
                    "values (" +
                    "'" + job.command + "' , " +
                    job.month         + "  , " +
                    job.m_day         + "  , " +
                    job.w_day         + "  , " +
                    job.hour          + "  , " +
                    job.minute        + "    " +
                    ");");
        }
        catch ( Exception e ) {
             System.err.println(e.getMessage());
        }
        finally {
            DBConn.closeConn(conn);
        }
    }

    /**
     * Get nullable integer from {@link ResultSet}.
     * @param  rs  {@link ResultSet} from which result will e extracted
     * @param  what  index if wanted {@link Integer}
     * @return  nullable {@link Integer}
     */
    private static Integer resultGetNInt(ResultSet rs, String what)
    {
        Integer result = null;
        try {
            result = rs.getObject(what) == null ? null : rs.getInt(what);
        }
        catch ( Exception e ) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    /**
     * Get all execution records from Kronos database.
     * @return  list of {@link Job} objects
     */
    public static List<Job> getAll()
    {
        List<Job> result = new LinkedList<>();
        Connection conn = DBConn.establishConn();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM jobs");

            while ( rs.next() ) {
                Integer id       = rs.getInt("id");
                String  command  = rs.getString("command");
                Integer month    = resultGetNInt(rs, "month");
                Integer m_day    = resultGetNInt(rs, "m_day");
                Integer w_day    = resultGetNInt(rs, "w_day");
                Integer hour     = resultGetNInt(rs, "hour");
                Integer minute   = resultGetNInt(rs, "minute");
                result.add(new Job(id, command, month, m_day, w_day, hour, minute));
            }
        }
        catch ( Exception e ) {
             System.err.println(e.getMessage());
        }
        finally {
            DBConn.closeConn(conn);
        }

        return result;
    }

    /**
     * Joins jobs with executions.
     * @return  list of executions along with {@link Job} command
     */
    public static List<String[]> getExecuted()
    {
        List<String[]> result = new LinkedList<>();
        Connection conn = DBConn.establishConn();

        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT command, date, exit_code, exit_output " +
                    "FROM executions INNER JOIN jobs ON executions.job_id = jobs.id";
            ResultSet rs = stmt.executeQuery(query);

            while ( rs.next() ) {
                String command      = rs.getString("command");
                String date         = rs.getString("date");
                String exit_code    = resultGetNInt(rs, "exit_code").toString();
                String exit_output  = rs.getString("exit_output");
                result.add(new String [] {command, date, exit_code, exit_output});
            }
        }
        catch ( Exception e ) {
            System.err.println(e.getMessage());
        }
        finally {
            DBConn.closeConn(conn);
        }

        return result;
    }

}
