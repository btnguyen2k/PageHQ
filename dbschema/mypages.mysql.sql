DROP TABLE IF EXISTS mypages_account;
CREATE TABLE mypages_account (
    uemail                            VARCHAR(128),
    utimestamp_create                 TIMESTAMP,
        INDEX (utimestamp_create),
    PRIMARY KEY (uemail)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS mypages_page;
CREATE TABLE mypages_page (
    pid                                VARCHAR(32),
    padmin_email                       VARCHAR(128),
    ptimestamp_create                  TIMESTAMP,
        INDEX (ptimestamp_create),
    ptimestamp_lastactive              DATETIME,
        INDEX (ptimestamp_lastactive),
    pstatus                            INT                        NOT NULL DEFAULT 0,
    psettings                          TEXT,
    PRIMARY KEY (pid, padmin_email)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS mypages_master_feed;
CREATE TABLE mypages_master_feed (
    feed_id                            VARCHAR(32),
    feed_type                          INT                        NOT NULL DEFAULT 0,                        
    fuser_email                        VARCHAR(128),
        INDEX (fuser_email),
    fpage_id                           VARCHAR(32),
        INDEX (fpage_id),
    ftimestamp                         DATETIME,
        INDEX (ftimestamp),
    fnum_likes                         INT                        NOT NULL DEFAULT 0,
    fnum_shares                        INT                        NOT NULL DEFAULT 0,
    fnum_comments                      INT                        NOT NULL DEFAULT 0,
    PRIMARY KEY (feed_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
