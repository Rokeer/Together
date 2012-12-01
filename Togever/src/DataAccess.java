import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataAccess {

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	public DataAccess() {

	}

	public void saveLocation(String uname, String ulat, String ulng)
			throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			statement.execute("INSERT INTO userdata VALUES('" + uname + "', '"
					+ ulng + "', '" + ulat
					+ "','','') ON DUPLICATE KEY UPDATE ulng = '" + ulng
					+ "', ulat = '" + ulat + "'");
			System.out.println("update done");
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}
	
	public void saveHB(String uname, String lastmsg)throws Exception{
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			statement.execute("update TOSERVER.userdata set lastmsg = '"+lastmsg+"' where uname = '"+uname+"'");
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}
	
	public boolean signup(String mStrMSG) throws Exception{
		try {
			String info[] = mStrMSG.split("&");
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TOSERVER.userdata where uname = '"
							+ info[0] + "'");
			if (resultSet.next())
			{
				close();
				return false;
			} else {
				statement.execute("insert into TOSERVER.userdata values('"+info[0]+"','0','0','"+info[1]+"','')");
			}
			close();
			return true;

		} catch (Exception e) {
			throw e;
		}
	}

	public String getHB(String uname) throws Exception {
		try {
			String lastTime = "0";
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TOSERVER.userdata where uname = '"
							+ uname + "'");
			while (resultSet.next()) {
				if (resultSet.getString("uname").equals(uname)) {
					lastTime = resultSet.getString("lastmsg");
					break;
				}
			}
			return lastTime;

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public String getLocation(String uname) throws Exception {
		try {
			String locate = "0";
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			ResultSet tmpresultSet = statement
					.executeQuery("select * from TOSERVER.userdata where uname = '"
							+ uname + "'");
			if (!tmpresultSet.wasNull()) {
				while (tmpresultSet.next()) {
					if (tmpresultSet.getString("uname").equals(uname)) {
						locate = tmpresultSet.getString("ulng") + ","
								+ tmpresultSet.getString("ulat");
						break;
					}
				}
				tmpresultSet.close();
				return locate;
			} else {
				tmpresultSet.close();
				return "Cannot find this people"; // i don't think this will
													// happen.
			}
			

		} catch (Exception e) {
			throw e;
		}
	}

	public ResultSet getList(String table, String uname) throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from TOSERVER."
					+ table + " where uname = '" + uname + "'");
			return resultSet;

		} catch (Exception e) {
			throw e;
		}
	}

	public boolean addList(String table, String uname, String tname)
			throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from "+table+" where uname='"+uname+"' && tname='"+tname+"'");
			if (!resultSet.next()){
				statement.execute("insert into TOSERVER." + table + " values('"
						+ uname + "', '" + tname + "')");
				if (table.equals("blocklist")) {
					statement.execute("insert into TOSERVER.wasblocked values('"
							+ tname + "', '" + uname + "')");
				}
				return true;
			} else {
				return false;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public void delList(String table, String uname, String tname)
			throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			statement.execute("delete from TOSERVER." + table
					+ " where uname='" + uname + "' && tname='" + tname + "'");
			if (table.equals("blocklist")) {
				statement
						.execute("delete from TOSERVER.wasblocked where uname='"
								+ tname + "' && tname='" + uname + "'");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public boolean wasBlocked(String uname, String tname) throws Exception {
		try {
			String tmpwd = "";
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TOSERVER.wasblocked where uname='"
							+ uname + "' && tname='" + tname + "'");
			if (!resultSet.next()) {
				return false;
			}
			return true;

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public boolean checkIdentity(String mName, String mPwd) throws Exception {
		try {
			String tmpwd = "";
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TOSERVER.userdata");
			while (resultSet.next()) {
				if (resultSet.getString("uname").equals(mName)) {
					tmpwd = resultSet.getString("upwd");
					break;
				}
			}
			return (tmpwd.equals(mPwd));

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public boolean isInArea(String uname1, String uname2) throws Exception {

		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost/TOSERVER", "rokeer", "enjoylove");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from TOSERVER.userdata");

			return calculateDis(resultSet, uname1, uname2);
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	public boolean calculateDis(ResultSet resultSet, String uname1,
			String uname2) throws SQLException {
		// ResultSet is initially before the first data set
		String ulat1 = "0", ulng1 = "0", ulat2 = "0", ulng2 = "0";
		while (resultSet.next()) {
			if (resultSet.getString("uname").equals(uname1)) {
				ulat1 = resultSet.getString("ulat");
				ulng1 = resultSet.getString("ulng");
				System.out.println(ulat1);
				System.out.println(ulng1);
			}
			if (resultSet.getString("uname").equals(uname2)) {
				ulat2 = resultSet.getString("ulat");
				ulng2 = resultSet.getString("ulng");
				System.out.println(ulat2);
				System.out.println(ulng2);
			}
		}

		double dlat1 = Double.parseDouble(ulat1);
		double dlng1 = Double.parseDouble(ulng1);
		double dlat2 = Double.parseDouble(ulat2);
		double dlng2 = Double.parseDouble(ulng2);

		Distance caldis = new Distance(dlat1, dlng1, dlat2, dlng2);

		if (caldis.getDistance() <= 500) {
			return true;
		} else {
			return false;
		}

	}

	public void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

}