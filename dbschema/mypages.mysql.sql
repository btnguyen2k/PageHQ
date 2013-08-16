DROP TABLE IF EXISTS mypages_account;
CREATE TABLE mypages_account (
	uemail							VARCHAR(128),
	utimestamp_create				TIMESTAMP,
		INDEX (utimestamp_create),
	PRIMARY KEY (uemail)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS mypages_page;
CREATE TABLE mypages_page (
	pid								VARCHAR(32),
	padmin_email					VARCHAR(128),
	ptimestamp_create				TIMESTAMP,
		INDEX (ptimestamp_create),
	ptimestamp_lastactive			DATETIME,
		INDEX (ptimestamp_lastactive),
	pstatus							INT						NOT NULL DEFAULT 0,
	psettings						TEXT,
	PRIMARY KEY (pid, padmin_email)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
