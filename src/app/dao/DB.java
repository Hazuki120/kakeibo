package app.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

	private static final String url =
		"jdbc:mysql://localhost:3307/kakeibo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";
	private static final String user = "appuser";	// docker-compose.yml の MYSQL_USER
	private static final String pass = "apppass";	// docker-compose.yml の MYSQL_PASSWORD
	
	public static Connection getConnection() throws SQLException{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			throw new SQLException("JDBC ドライバが見つかりません", e);
		}
		return DriverManager.getConnection(url, user, pass);
	}
}