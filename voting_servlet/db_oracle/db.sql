commit;

CREATE TABLE USERS(
VOTER_ID VARCHAR2(20) NOT NULL,
PASSWORD VARCHAR2(20) NOT NULL
);

CREATE TABLE VOTES1(
VOTER_ID VARCHAR2(20) NOT NULL,
CANDIDATE VARCHAR2(20) NOT NULL
);
--This ensures login queries are optimized.

CREATE INDEX idx_users_voter ON USERS(VOTER_ID);


--This ensures each voter votes only once and optimizes the lookup.
CREATE UNIQUE INDEX idx_votes_voter ON VOTES1(VOTER_ID);


---? Indexing ? Faster queries (O(log N) instead of O(N))
--? Optimized SQL Queries ? Eliminates unnecessary fetches



DROP TABLE VOTES1;
DROP TABLE USERS;
SELECT * FROM VOTES1;

delete from votes1;

insert into users values ('7123456','daryl123');
SELECT * FROM USERS;