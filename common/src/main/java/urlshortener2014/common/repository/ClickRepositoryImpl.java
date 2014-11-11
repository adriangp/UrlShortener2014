package urlshortener2014.common.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import urlshortener2014.common.domain.Click;

@Repository
public class ClickRepositoryImpl implements ClickRepository {
	
	private static final Logger log = LoggerFactory.getLogger(ClickRepositoryImpl.class);

	private static final RowMapper<Click> rowMapper = new RowMapper<Click>() {
        @Override
		public Click mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Click(
					rs.getLong("id"), 
					rs.getString("hash"), 
					rs.getDate("created"),
					rs.getString("referrer"),
					rs.getString("browser"),
					rs.getString("platform"),
					rs.getString("ip"),
					rs.getString("country"));
        }
    };
	
	@Autowired
    protected JdbcTemplate jdbc;

	public ClickRepositoryImpl(){
	}
	
	public ClickRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public List<Click> findByHash(String hash) {
		try {
			return jdbc.query("SELECT * FROM click WHERE hash=?", new Object[]{hash}, rowMapper);
		} catch (Exception e) {
			log.debug("When select for hash "+hash, e);
			return null;
		}
	}

	@Override
	public Click save(Click cl) {
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			jdbc.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection conn)
						throws SQLException {
					PreparedStatement ps = conn.prepareStatement("INSERT INTO CLICK VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					ps.setNull(1, Types.BIGINT);
					ps.setString(2,  cl.getHash());
					ps.setDate(3,  cl.getCreated());
					ps.setString(4,  cl.getReferrer());
					ps.setString(5,  cl.getBrowser());
					ps.setString(6,  cl.getPlatform());
					ps.setString(7,  cl.getIp());
					ps.setString(8,  cl.getCountry());
					return ps;
				}
			}, holder);
			cl.setId(holder.getKey().longValue());
		} catch (DuplicateKeyException e) {
			log.debug("When insert for click with id "+cl.getId(), e);
			return cl;
		} catch (Exception e) {
			log.debug("When insert a click", e);
			return null;
		}
		return cl;
	}

}
