-- ShortURL

CREATE TABLE SHORTURL(
	HASH		VARCHAR(10) PRIMARY KEY,	-- Key
	TARGET		VARCHAR(1024),				-- Original URL
	CREATED 	TIMESTAMP,					-- Creation date
	OWNER		VARCHAR(255),				-- User id
	MODE		INTEGER						-- Redirect mode
);

-- Click

CREATE TABLE CLICK(
    ID 			BIGINT IDENTITY,			-- KEY
	HASH 		VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES SHORTURL(HASH),	-- Foreing key	
	CREATED 	TIMESTAMP,					-- Creation date
	REFERRER	VARCHAR(1024),				-- Traffic origin
	BROWSER		VARCHAR(50),				-- Browser
	PLATFORM	VARCHAR(50),				-- Platform
	IP			VARCHAR(20),				-- IP
	COUNTRY		VARCHAR(50)					-- Country
)