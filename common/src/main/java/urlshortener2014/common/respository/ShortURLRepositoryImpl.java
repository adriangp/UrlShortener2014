package urlshortener2014.common.respository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import urlshortener2014.common.domain.ShortURL;

@Repository
public class ShortURLRepositoryImpl implements ShortURLRepository {
	
	private static Logger logger = LoggerFactory.getLogger(ShortURLRepositoryImpl.class);

	private static final RowMapper<ShortURL> rowMapper = new RowMapper<ShortURL>() {
        @Override
		public ShortURL mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new ShortURL(rs.getString("hash"), rs.getString("target"), null);
        }
    };
	
	@Autowired
    protected JdbcTemplate jdbc;

	public ShortURLRepositoryImpl(){
	}
	
	public ShortURLRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public ShortURL findByKey(String id) {
		try {
		return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash=?", rowMapper, id);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ShortURL save(ShortURL su) {
		try {
			jdbc.update("INSERT INTO shorturl VALUES (?,?)", su.getHash(), su.getTarget());
		} catch (DuplicateKeyException e) {
			return su;
		} catch (Exception e) {
			return null;
		}
		return su;
	}

}
